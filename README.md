# Document Similarity using MapReduce

## Project Overview

This project computes the **Jaccard Similarity** between multiple text documents using the **Apache Hadoop MapReduce framework**. The Jaccard Similarity measures the similarity between two sets of words in documents, calculated as:

\[ Jaccard Similarity (A, B) = |A \cap B| / |A \cup B| \]

The analysis includes:
- Tokenizing document text and extracting unique words.
- Computing word intersections and unions for document pairs.
- Calculating the Jaccard Similarity score for each document pair.

## Approach and Implementation

### **Mapper (DocumentSimilarityMapper)**
- Reads input text, where each line represents a document.
- Extracts the document ID and content.
- Tokenizes the content into unique lowercase words.
- Emits (word, document ID) pairs for further processing.

### **Reducer (DocumentSimilarityReducer)**
- Aggregates document IDs for each unique word.
- Constructs term sets for each document.
- Computes the Jaccard Similarity between document pairs based on shared terms.
- Emits (document pair, similarity score).

## Execution Steps

### 1. **Start the Hadoop Cluster**
```bash
docker compose up -d
```

### 2. **Build the Code**
```bash
mvn install
```

### 3. **Move jar files**
```bash
mv target/*.jar shared-folder/input/code/
```

### 3. **Copy JAR to Docker Container**
```bash
docker cp shared-folder/input/code/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 4. **Move Dataset to Docker Container**
```bash
docker cp shared-folder/input/data/doc.txt res
ourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Connect to Docker Container**
```bash
docker exec -it resourcemanager /bin/bash
```

### 6. **Navigate to the Hadoop directory**
```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Make the directory**
```bash
hadoop fs -mkdir -p /input/dataset
```

### 8. **Navigate doc to dataset**
```bash
hadoop fs -put ./doc.txt /input/dataset/
```

### 9. **Execute the MapReduce Job**
```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/dataset /output
```

### 9. **View the Output**
```bash
hadoop fs -cat /output/part-r-00000
```

### 10. **Copy Output from HDFS to Local OS**
```bash
hdfs dfs -get /output /opt/hadoop-3.2.1/share/hadoop/mapreduce/
exit
docker cp resourcemanager:/opt/hadoop-3.2.1/share
/hadoop/mapreduce/output/ shared-folder/output/
```

## Challenges Faced & Solutions

### **1. Challenge: Docker `cp` command failure**
- **Issue:** `docker cp` command failed if the resourcemanager container was not running.
- **Solution:** Verified container status using `docker ps` and ensured destination path exists inside the container.

### **2. Challenge: Output file already exists error**
- **Issue:** Hadoop job failed due to existing output files.
- **Solution:** Removed output directory before execution:
  ```bash
  hadoop fs -rm -r /output
  ```

### **3. Challenge: HDFS directory or file errors**
- **Issue:** Errors like "No such file or directory" or "Permission denied" when copying files.
- **Solution:** Verified HDFS was running using:
  ```bash
  hadoop fs -ls /
  ```

### **4. Challenge: Job execution failures**
- **Issue:** Job failed due to missing JAR, incorrect class name, or insufficient cluster resources.
- **Solution:** Ensured JAR file was correctly copied and verified using:
  ```bash
  ls /opt/hadoop-3.2.1/share/hadoop/mapreduce/
  ```

## Sample Output

### **Input Format:**
```
doc1    People go to walk everyday 
doc2    walk is important for many things  
doc3    walk makes people healthy
```

### **Expected Output:**
```
<doc3, doc2>	 -> 11.11%
<doc3, doc1>	 -> 28.57%
<doc2, doc1>	 -> 10.00%

```

