package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        List<InputData> inputData = fileManager.loadFile("InputSample.csv");
        List<OutputData> outputData = Main.processFile(inputData);
        fileManager.storeFile("OutputSample_" + System.currentTimeMillis() + ".csv", outputData);
    }

    public static List<OutputData> processFile(List<InputData> inputData) {
        List<OutputData> result = new ArrayList<>();
        for (int i = 0; i < inputData.size(); i++) {
            for (int j = i + 1; j < inputData.size(); j++) {
                InputData source = inputData.get(i);
                InputData match = inputData.get(j);
                boolean isAccurate = checkCompatibility(source, match);
                // DEBUG results
                if(isAccurate){
                    System.out.println(source);
                    System.out.println(match);
                    System.out.println();
                }
                result.add(new OutputData(source.id(), match.id(), isAccurate));
            }
        }
        return result;
    }

    // Equal first name and last name could be common
    // Equal email indicates something
    // Equal last name and address indicate just people from same family
    public static boolean checkCompatibility(InputData source, InputData match) {
        // check partial email has no value, could be improved latter
        int email = checkFieldCompatibility(source.email(), match.email());
        int address = checkFieldCompatibility(source.address(), match.address());

        // Same email and house, same person
        if (address + email == 20) {
            return true;
        }
        int firstName = checkFieldCompatibility(source.firstName(), match.firstName());
        int lastName = checkFieldCompatibility(source.lastName(), match.lastName());

        // Contains one equal name and one partial, so one of them could be an abbreviation
        int nameMatch = firstName + lastName;
        if (nameMatch >= 15) {
            // partial full name, same house, same person
            if (address == 10) {
                return true;
            }
            // partial full name, the address could be outdated, but the email stays the same
            if (email == 10) {
                return true;
            }
            // Here we have the same equal name

            if (nameMatch == 20) {
                // with the partial address, this could mean an abbreviation
                if (address == 5) {
                    int zipCode = checkFieldCompatibility(source.postalZip(), match.postalZip());
                    // possibly typo on address
                    return zipCode == 10;
                }
            }
        }
        return false;
    }

    // Partial = 5
    // Full = 10
    public static int checkFieldCompatibility(String source, String match) {
        if (source == null || source.isBlank() || match == null || match.isBlank()) {
            return 0;
        }
        boolean partialMatch = false;
        if (source.equals(match)) {
            return 10;
        } else if (source.length() > match.length()) {
            partialMatch = source.startsWith(match);
        } else if (match.length() > source.length()) {
            partialMatch = match.startsWith(source);
        }
        return partialMatch ? 5 : 0;
    }

}