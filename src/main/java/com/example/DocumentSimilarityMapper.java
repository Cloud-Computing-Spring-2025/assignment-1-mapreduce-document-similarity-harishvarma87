package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {

    @Override
    public void map(Object inputKey, Text inputValue, Context context) throws IOException, InterruptedException {
        String lineContent = inputValue.toString();
        String[] lineParts = lineContent.split("\\s+", 2);

        if (lineParts.length < 2)
            return; // Skip malformed entries

        String docId = lineParts[0];
        String documentContent = lineParts[1];

        HashSet<String> uniqueTerms = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(documentContent);

        while (tokenizer.hasMoreTokens()) {
            uniqueTerms.add(tokenizer.nextToken().toLowerCase());
        }

        // Emit (word, docID) for further grouping in the reducer
        for (String term : uniqueTerms) {
            context.write(new Text(term), new Text(docId));
        }
    }
}