package com.adimodi96.mnist;

public class Result {

    private int number;
    private float probability;
    private long timeTaken;

    public Result(float[] probabilities, long timeTaken) {
        this.number = argmax(probabilities);
        this.probability = ((int) (probabilities[number] * 100) / 100.0f);
        this.timeTaken = timeTaken;
    }

    private static int argmax(float[] probabilities) {
        int maxIdx = -1;
        float maxProbability = 0.0f;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > maxProbability) {
                maxProbability = probabilities[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    public int getNumber() {
        return number;
    }

    public float getProbability() {
        return probability;
    }

    public long getTimeTaken() {
        return timeTaken;
    }
}