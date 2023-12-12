package com.docparser.springboot.service;

import com.docparser.springboot.utils.ParsingUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NLPService {

    // Method to find the main topic in a given text
    public String findMainTopic(String text) {
        // NLP pipeline setup
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // Processing the text
        Annotation annotation = pipeline.process(text);
        // List to store potential main topics
        List<String> topics = new ArrayList<>();
        // Iterating over sentences
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Default main topic
            String mainTopic;
            // Iterating over tokens (words) in the sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // Getting part of speech of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // Checking if the token is a noun
                if ("NN".equals(pos) || "NNS".equals(pos) || "NNP".equals(pos) || "NNPS".equals(pos)) {
                    // Setting the token as the main topic
                    mainTopic = token.originalText();
                    topics.add(mainTopic);
                }
            }
        }
        // Determining the most frequent noun as the main topic
        Map.Entry<String, Long> itemCounts = topics.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        if (itemCounts == null)
            return "HeadingText";

        return itemCounts.getKey();
    }

    // Method to find the most common word in a paragraph, excluding stop words
    public String findMostCommonWord(String paragraph, Set<String> stopWords) {
        // Breaking the paragraph into lines
        String[] lines = ParsingUtils.countLines(paragraph);
        // Map to track word frequencies
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                // Cleaning and normalizing the word
                word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                // Skipping stop words
                if (!stopWords.contains(word)) {
                    // Updating the frequency map
                    wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
                }
            }
        }
        // Finding the most common word
        String mostCommonWord = null;
        int maxFrequency = 0;
        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostCommonWord = entry.getKey();
            }
        }
        return mostCommonWord;
    }

    public String hyphenateText(String text) {
        String[] words = text.split("\\s+");
        StringBuilder syllabledText = new StringBuilder();
        // Iterating over each word
        for (String word : words) {
            // Hyphenating the word and appending to the result
            syllabledText.append(hyphenateWords(word)).append(" ");
        }
        return syllabledText.toString().trim();
    }

    public String hyphenateWords(String word) {
        /**
         * Basic pattern: Vowel followed by non-vowels (greedy), then optional
         * non-vowels
         * This is a simplistic pattern and won't work correctly for all English words
         */
        String pattern = "([aeiouy]+[^aeiouy]*)([^aeiouy]*)";
        return word.replaceAll(pattern, "$1-$2").replaceAll("-{2,}", "-").replaceAll("-$", "");
    }

    public List<String> identifySyllables(String word) {
        List<String> syllables = new ArrayList<>();
        String[] vowels = { "a", "e", "i", "o", "u", "y" };
        StringBuilder currentSyllable = new StringBuilder();
        boolean lastCharVowel = false;
        for (int i = 0; i < word.length(); i++) {
            String currentChar = word.substring(i, i + 1).toLowerCase();
            boolean currentCharVowel = false;
            for (String vowel : vowels) {
                if (vowel.equals(currentChar)) {
                    currentCharVowel = true;
                    break;
                }
            }
            if (currentCharVowel) {
                if (!lastCharVowel) {
                    syllables.add(currentSyllable.toString());
                    currentSyllable = new StringBuilder();
                }
                lastCharVowel = true;
            } else {
                currentSyllable.append(currentChar);
                lastCharVowel = false;
            }
        }
        if (!currentSyllable.isEmpty()) {
            syllables.add(currentSyllable.toString());
        }
        return syllables;
    }

    public String hyphenateWord(String word) {
        List<String> syllables = identifySyllables(word);
        StringBuilder hyphenatedWord = new StringBuilder();
        for (int i = 0; i < syllables.size(); i++) {
            hyphenatedWord.append(syllables.get(i));
            if (i < syllables.size() - 1) {
                hyphenatedWord.append("-");
            }
        }
        return hyphenatedWord.toString();
    }

}
