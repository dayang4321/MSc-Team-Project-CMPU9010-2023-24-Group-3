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

    // Method to find the intent of a given text
    public void findIntent() {
        String text = "Java 17, released in September 2021, marks a significant milestone in the " +
                "evolution of the Java programming language. This version brings forth a " +
                "plethora of new features, enhancements, and performance improvements, making " +
                "it a highly anticipated release in the Java community.  In this essay, we will " +
                "explore the key features of Java 17 and delve into its diverse range of use cases";
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse");
        // Creating a pipeline with the given properties
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // Processing the text with the pipeline
        Annotation annotation = pipeline.process(text);
        // List to store topics or intents found
        List<String> topics = new ArrayList<>();
        // Iterating over sentences in the annotated text
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Getting the semantic graph of the sentence
            SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
            // Default intent if none is found
            String intent = "It does not seem that the sentence expresses an explicit intent.";
            // Iterating over the edges in the semantic graph
            for (SemanticGraphEdge edge : sg.edgeIterable()) {
                // Checking if the edge represents a direct object
                if (edge.getRelation().getLongName() == "direct object") {
                    // Extracting the verb and the direct object
                    String tverb = edge.getGovernor().originalText();
                    String dobj = edge.getDependent().originalText();
                    // Forming a phrase that represents an intent
                    dobj = dobj.substring(0, 1).toUpperCase() + dobj.substring(1).toLowerCase();
                    intent = tverb + dobj;
                }
                topics.add(intent);
            }
            // Printing the sentence and its detected intent
            System.out.println("Sentence:\t" + sentence);
            System.out.println("Intent:\t\t" + intent + "\n");
        }
        // Printing all detected intents
        System.out.println("Intent:\t\t" + topics + "\n");
    }

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
            String mainTopic = "Main topic not found.";
            // Iterating over tokens (words) in the sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // Getting part of speech of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // Checking if the token is a noun
                if ("NN".equals(pos) || "NNS".equals(pos) || "NNP".equals(pos) || "NNPS".equals(pos)) {
                    // Setting the token as the main topic
                    mainTopic = token.originalText();
                    topics.add(mainTopic);
                    // Break after finding the first noun
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
        // Iterating over each line
        for (String line : lines) {
            // Splitting the line into words
            String[] words = line.split("\\s+");
            // Iterating over each word
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

    // Method to hyphenate a text
    public String hyphenateText(String text) {
        // Splitting the text into words
        String[] words = text.split("\\s+");
        StringBuilder syllabledText = new StringBuilder();
        // Iterating over each word
        for (String word : words) {
            // Hyphenating the word and appending to the result
            syllabledText.append(hyphenateWord(word)).append(" ");
        }
        return syllabledText.toString().trim();
    }

    // Method to hyphenate a single word
    public String hyphenateWord(String word) {
        // Pattern for identifying syllable boundaries
        String pattern = "([aeiouy]+[^aeiouy]*)([^aeiouy]*)";
        // Applying the pattern to insert hyphens
        return word.replaceAll(pattern, "$1-$2").replaceAll("-{2,}", "-").replaceAll("-$", "");
    }
}
