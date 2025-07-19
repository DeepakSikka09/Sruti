
package in.ecomexpress.sruti.utils;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

// This class represents a directed graph using adjacency list
// representation
public class Graph {
    private int V;   // No. of vertices
    private LinkedList<Integer>[] adj; //Adjacency Lists

    // Constructor
    Graph(int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList();
    }

    // Function to add an edge into the graph
    void addEdge(int v, int w) {
        adj[v].add(w);
    }

    // prints BFS traversal from a given source s
    void BFS(int s) {
        // Mark all the vertices as not visited(By default
        // set as false)
        System.out.println("Start BFS");
        boolean[] visited = new boolean[V];

        // Create a queue for BFS
        LinkedList<Integer> queue = new LinkedList<Integer>();

        // Mark the current node as visited and enqueue it
        visited[s] = true;
        queue.add(s);

        System.out.println("visited["+s+"] true");


        while (queue.size() != 0) {
            System.out.println("queue size: "+queue.size());
            // Dequeue a vertex from queue and print it
            s = queue.poll();
            System.out.println("queue.poll() :"+s);


            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            Iterator<Integer> i = adj[s].listIterator();


            System.out.println("--------------------");
            System.out.println();
            while (i.hasNext()) {

                System.out.println("--------------------");

                System.out.println();
                int n = i.next();
                System.out.println("n: "+n);
                if (!visited[n]) {
                    visited[n] = true;
                    System.out.println("visited["+n+"] true");
                    System.out.println("queue.add("+n+")");
                    queue.add(n);
                }
            }
        }
    }

    // Driver method to
    public static void main(String[] args) {
        Graph g = new Graph(4);

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 0);
        g.addEdge(2, 3);
        g.addEdge(3, 3);

        System.out.println("Following is Breadth First Traversal " + "(starting from vertex 2)");

        g.BFS(2);
    }
}


/*
https://github.com/bugsee/examples-presigned-url-upload

class UploadTask extends AsyncTask<String, Void, AsyncTaskResult<Void>> {
    private static final int NETWORK_TIMEOUT_SEC = 60;
    private static final String OUR_SERVER_URL = "http://192.168.0.26:3000/users/testUser/objects";

    @Override
    protected AsyncTaskResult doInBackground(String... params) {
        try {
            // Obtain the url
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
                    .connectTimeout(NETWORK_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .readTimeout(NETWORK_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .writeTimeout(NETWORK_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .build();
            Request getUrlRequest = new Request.Builder()
                    .url(OUR_SERVER_URL)
                    .post(RequestBody.create(MediaType.parse("text/plain"), ""))
                    .build();
            Response getUrlResponse = client.newCall(getUrlRequest).execute();
            if (!getUrlResponse.isSuccessful())
                return new AsyncTaskResult(new Exception("Get url response code: " + getUrlResponse.code()));
            String responseJsonString = getUrlResponse.body().string();
            JSONObject getUrlResponseJson = new JSONObject(responseJsonString);
            String url = getUrlResponseJson.getString("url");
            // Upload the file
            String imagePath = params[0];
            Request uploadFileRequest = new Request.Builder().url(url)
                    .put(RequestBody.create(MediaType.parse(""), new File(imagePath)))
                    .build();
            Response uploadResponse = client.newCall(uploadFileRequest).execute();
            if (!uploadResponse.isSuccessful())
                return new AsyncTaskResult(new Exception("Upload file response code: " + uploadResponse.code()));
            return new AsyncTaskResult(null);
        } catch (Exception e) {
            return new AsyncTaskResult(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult result) {
        super.onPostExecute(result);
        if (result.hasError()) {
            Log.e(TAG, "UploadTask failed", result.getError());
            Toast.makeText(MainActivity.this, "UploadTask finished with error", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "UploadTask finished successfully", Toast.LENGTH_LONG).show();
        }
    }
}
*/
