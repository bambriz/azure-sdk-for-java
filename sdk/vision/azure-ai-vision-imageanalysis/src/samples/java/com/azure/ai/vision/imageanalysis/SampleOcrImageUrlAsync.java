// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
//
// DESCRIPTION:
//     This sample demonstrates how to extract printed or hand-written text for of a publicly accessible
//     image URL, using an asynchronous client.
//
//     The asynchronous `analyze` method call returns an `ImageAnalysisResult` object.
//     A call to `getRead()` on the result will return a `ReadResult` object. It includes a list of 
//     `DetectedTextBlock` objects. Currently, the list will always contain one block, as the service does 
//     not yet support grouping text lines into separate blocks. The `DetectedTextBlock` object contains a 
//     list of `DetectedTextLine` object. Each one includes: 
//     - The text content of the line.
//     - A polygon coordinates in pixels, for a polygon surrounding the line of text in the image.
//     - A list of `DetectedTextWord` objects.
//     Each `DetectedTextWord` object contains:
//     - The text content of the word.
//     - A polygon coordinates in pixels, for a polygon surrounding the word in the image.
//     - A confidence score in the range [0, 1], with higher values indicating greater confidences in
//       the recognition of the word. 
//
// USAGE:
//     Compile the sample:
//         mvn clean dependency:copy-dependencies
//         javac SampleOcrImageUrlAsync.java -cp target\dependency\*
//     Run the sample:
//         java -cp ".;target\dependency\*" SampleOcrImageUrlAsync
//
//     Set these two environment variables before running the sample:
//     1) VISION_ENDPOINT - Your endpoint URL, in the form https://your-resource-name.cognitiveservices.azure.com
//                          where `your-resource-name` is your unique Azure Computer Vision resource name.
//     2) VISION_KEY - Your Computer Vision key (a 32-character Hexadecimal number)

import com.azure.ai.vision.imageanalysis.ImageAnalysisAsyncClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.DetectedTextLine;
import com.azure.ai.vision.imageanalysis.models.DetectedTextWord;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisResult;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.KeyCredential;
import java.net.URL;
import java.util.Arrays;

public class SampleOcrImageUrlAsync {

    public static void main(String[] args) {

        String endpoint = System.getenv("VISION_ENDPOINT");
        String key = System.getenv("VISION_KEY");

        if (endpoint == null || key == null) {
            System.out.println("Missing environment variable 'VISION_ENDPOINT' or 'VISION_KEY'.");
            System.out.println("Set them before running this sample.");
            System.exit(1);
        }

        // Create an asynchronous Image Analysis client.
        ImageAnalysisAsyncClient client = new ImageAnalysisClientBuilder()
            .endpoint(endpoint)
            .credential(new KeyCredential(key))
            .buildAsyncClient();

        try {
            // Extract text from an input image URL. This is an synchronous (non-blocking) call, but here we block until the service responds.
            ImageAnalysisResult result = client.analyze(
                new URL("https://aka.ms/azsdk/image-analysis/sample.jpg"), // imageUrl: the URL of the image to analyze
                Arrays.asList(VisualFeatures.READ), // visualFeatures
                null)
                .block();

            printAnalysisResults(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Print analysis results to the console
    public static void printAnalysisResults(ImageAnalysisResult result) {

        System.out.println("Image analysis results:");
        System.out.println(" Read:");
        for (DetectedTextLine line : result.getRead().getBlocks().get(0).getLines()) {
            System.out.println("   Line: '" + line.getText()
                + "', Bounding polygon " + line.getBoundingPolygon());
            for (DetectedTextWord word : line.getWords()) {
                System.out.println("     Word: '" + word.getText()
                    + "', Bounding polygon " + word.getBoundingPolygon()
                    + ", Confidence " + String.format("%.4f", word.getConfidence()));
            }
        }
        System.out.println(" Image height = " + result.getMetadata().getHeight());
        System.out.println(" Image width = " + result.getMetadata().getWidth());
        System.out.println(" Model version = " + result.getModelVersion());
    }
}
