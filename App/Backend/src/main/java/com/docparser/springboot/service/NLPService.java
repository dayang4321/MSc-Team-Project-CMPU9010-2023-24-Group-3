package com.docparser.springboot.service;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class NLPService {


    public  void findIntent()
    {
        String text="Java 17, released in September 2021, marks a significant milestone in the " +
                "evolution of the Java programming language. This version brings forth a " +
                "plethora of new features, enhancements, and performance improvements, making " +
                "it a highly anticipated release in the Java community.  In this essay, we will " +
                "explore the key features of Java 17 and delve into its diverse range of use cases";
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(text);
        for(CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class))
        {
            SemanticGraph sg =
                    sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
            String intent = "It does not seem that the sentence expresses an explicit intent.";
            for (SemanticGraphEdge edge : sg.edgeIterable()) {
                if (edge.getRelation().getLongName() == "direct object"){
                    String tverb = edge.getGovernor().originalText();
                    String dobj = edge.getDependent().originalText();
                    dobj = dobj.substring(0,1).toUpperCase() + dobj.substring(1).toLowerCase();
                    intent = tverb + dobj;
                }
            }
            System.out.println("Sentence:\t" + sentence);
            System.out.println("Intent:\t\t" + intent + "\n");

        }
    }
    public void findMainTopic() {
        String text = "Java 17, released in September 2021, marks a significant milestone in the " +
                "evolution of the Java programming language. This version brings forth a " +
                "plethora of new features, enhancements, and performance improvements, making " +
                "it a highly anticipated release in the Java community. In this essay, we will " +
                "explore the key features of Java 17 and delve into its diverse range of use cases.";

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(text);
        List<String> topics= new ArrayList<>();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String mainTopic = "Main topic not found.";
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if ("NN".equals(pos) || "NNS".equals(pos) || "NNP".equals(pos) || "NNPS".equals(pos)) {
                    mainTopic = token.originalText();
                    topics.add(mainTopic);
                     // Break after finding the first noun
                }
            }


            System.out.println("Sentence:\t" + sentence);
            System.out.println("Main Topic:\t" + mainTopic + "\n");
        }
    }
}
