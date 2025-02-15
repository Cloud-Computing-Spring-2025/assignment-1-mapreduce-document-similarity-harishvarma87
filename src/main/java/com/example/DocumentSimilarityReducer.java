package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    private final Map<String, Set<String>> documentTermMap = new HashMap<>();

    @Override
    public void reduce(Text term, Iterable<Text> documentIds, Context context)
            throws IOException, InterruptedException {
        List<String> documentList = new ArrayList<>();

        for (Text docId : documentIds) {
            String docName = docId.toString();
            documentList.add(docName);

            // Store terms for each document
            documentTermMap.putIfAbsent(docName, new HashSet<>());
            documentTermMap.get(docName).add(term.toString());
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> documentList = new ArrayList<>(documentTermMap.keySet());

        // Compare each document pair and calculate Jaccard Similarity
        for (int i = 0; i < documentList.size(); i++) {
            for (int j = i + 1; j < documentList.size(); j++) {
                String doc1 = documentList.get(i);
                String doc2 = documentList.get(j);

                double similarityScore = computeJaccardSimilarity(doc1, doc2);

                if (similarityScore > 0) {
                    String key = "<" + doc1 + ", " + doc2 + ">";
                    String value = " -> " + String.format("%.2f", similarityScore * 100) + "%";
                    context.write(new Text(key), new Text(value));
                }
            }
        }
    }

    private double computeJaccardSimilarity(String doc1, String doc2) {
        Set<String> termsDoc1 = documentTermMap.getOrDefault(doc1, new HashSet<>());
        Set<String> termsDoc2 = documentTermMap.getOrDefault(doc2, new HashSet<>());

        Set<String> intersection = new HashSet<>(termsDoc1);
        intersection.retainAll(termsDoc2); // Compute |A ∩ B|

        Set<String> union = new HashSet<>(termsDoc1);
        union.addAll(termsDoc2); // Compute |A ∪ B|

        if (union.isEmpty())
            return 0; // Avoid division by zero

        return (double) intersection.size() / union.size();
    }
}
