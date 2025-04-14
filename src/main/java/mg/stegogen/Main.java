package mg.stegogen;

import mg.stegogen.core.RandomGenerator;

public class Main {
    public static void main(String[] args) {
        int[] indices = new RandomGenerator(10).generateUniquePositions(10, 100);
        for (int i = 0; i < indices.length; i++) {
            System.out.println("Position " + i + ": " + indices[i]);
        }
    }
}
