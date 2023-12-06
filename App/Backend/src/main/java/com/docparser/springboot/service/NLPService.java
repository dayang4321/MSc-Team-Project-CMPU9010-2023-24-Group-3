package com.docparser.springboot.service;

import com.docparser.springboot.utils.ParsingUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NLPService {


    public void findIntent() {
        String text = "Java 17, released in September 2021, marks a significant milestone in the " +
                "evolution of the Java programming language. This version brings forth a " +
                "plethora of new features, enhancements, and performance improvements, making " +
                "it a highly anticipated release in the Java community.  In this essay, we will " +
                "explore the key features of Java 17 and delve into its diverse range of use cases";
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(text);
        List<String> topics = new ArrayList<>();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            SemanticGraph sg =
                    sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
            String intent = "It does not seem that the sentence expresses an explicit intent.";
            for (SemanticGraphEdge edge : sg.edgeIterable()) {
                if (edge.getRelation().getLongName() == "direct object") {
                    String tverb = edge.getGovernor().originalText();
                    String dobj = edge.getDependent().originalText();
                    dobj = dobj.substring(0, 1).toUpperCase() + dobj.substring(1).toLowerCase();
                    intent = tverb + dobj;
                }
                topics.add(intent);
            }
            System.out.println("Sentence:\t" + sentence);
            System.out.println("Intent:\t\t" + intent + "\n");

        }
        System.out.println("Intent:\t\t" + topics + "\n");
    }

    public String findMainTopic(String text) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(text);
        List<String> topics = new ArrayList<>();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String mainTopic = "Main topic not found.";
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if ("NN" .equals(pos) || "NNS" .equals(pos) || "NNP" .equals(pos) || "NNPS" .equals(pos)) {
                    mainTopic = token.originalText();
                    topics.add(mainTopic);
                    // Break after finding the first noun
                }
            }
        }
        Map.Entry<String, Long> itemCounts = topics.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        if (itemCounts == null)
            return "HeadingText";

        return itemCounts.getKey();
    }


    public String findMostCommonWord(String paragraph, Set<String> stopWords) {
        String[] lines = ParsingUtils.countLines(paragraph);
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        // Iterate through each line
        for (String line : lines) {
            // Split the line into words
            String[] words = line.split("\\s+");
            // Update word frequencies in the map, excluding stop words
            for (String word : words) {
                // Remove punctuation and convert to lowercase for better comparison
                word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                // Skip stop words
                if (!stopWords.contains(word)) {
                    // Update word frequency in the map
                    wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
                }
            }
        }
        // Find the word with the highest frequency
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

    public  String hyphenateText(String text) {
        String[] words = text.split("\\s+");
        StringBuilder syllabledText = new StringBuilder();

        for (String word : words) {
            syllabledText.append(hyphenateWord(word)).append(" ");
        }

        return syllabledText.toString().trim();
    }

    public String hyphenateWord(String word) {
        // Basic pattern: Vowel followed by non-vowels (greedy), then optional non-vowels
        // This is a simplistic pattern and won't work correctly for all English words
        String pattern = "([aeiouy]+[^aeiouy]*)([^aeiouy]*)";
        return word.replaceAll(pattern, "$1-$2").replaceAll("-{2,}", "-").replaceAll("-$", "");
    }
}

