import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import info.debatty.java.stringsimilarity.Levenshtein;
import info.debatty.java.stringsimilarity.LongestCommonSubsequence;

public class threshold {
	public static final Levenshtein distanceUtil = new Levenshtein();
    public static final LongestCommonSubsequence lcsDistanceUtil = new LongestCommonSubsequence();

    public static void main(String args[]) {

	// Getting Positive Set
	HashSet<String> positiveSet = getPositiveSet();

	/*****************************************************************************************************
	 * Using Levenshtein for the first Iteration. Computing candidate set
	 *****************************************************************************************************/
	
	// Calculating distance between pairs of dataSet.
	ArrayList<NamePair> levenshteinNamePairs = calculatePairsDistance(getDataSet(), "Levenshtein");
	
	// Storing the distance -> namePairs mapping
	HashMap<Double, ArrayList<String>> levenshteinDistanceNamePairMapping = getDistanceNamePairMapping(levenshteinNamePairs);
	
	// Getting distance between the pairs in positive set.
	ArrayList<NamePair> levenshteinPositveSetNamePairs = calculatePositivePairsDistance(positiveSet, "Levenshtein");
	
	// Computing the candidate set.
	HashSet<Double> levenshteinCandidateSet = getCandidateSet(positiveSet, levenshteinDistanceNamePairMapping);
	
	// Corollary-1
	for (int i = 0; i < levenshteinPositveSetNamePairs.size(); i++) {
		levenshteinCandidateSet.add(levenshteinPositveSetNamePairs.get(i).getDistance());
	}
	
	// Computing the final candidate set.
	HashSet<Double> finalLevenshteinCandidateSet = getFinalCandidateSet(positiveSet,levenshteinDistanceNamePairMapping, levenshteinCandidateSet);

	/*****************************************************************************************************
	 * Using LCS for the first Iteration. Computing candidate set
	 *****************************************************************************************************/
	
	// Calculating distance between pairs of dataSet.
	ArrayList<NamePair> lcsNamePairs = calculatePairsDistance(getDataSet(), "Lcs");
	
	// Storing the distance -> namePairs mapping
	HashMap<Double, ArrayList<String>> lcsDistanceNamePairMapping = getDistanceNamePairMapping(lcsNamePairs);
	
	// Getting distance between the pairs in positive set.
	ArrayList<NamePair> lcsPositveSetNamePairs = calculatePositivePairsDistance(positiveSet, "Lcs");
	
	// Computing the candidate set.
	HashSet<Double> lcsCandidateSet = getCandidateSet(positiveSet, lcsDistanceNamePairMapping);

	// Corollary-1
	for (int i = 0; i < lcsPositveSetNamePairs.size(); i++) {
		lcsCandidateSet.add(lcsPositveSetNamePairs.get(i).getDistance());
	}

	// Computing the final candidate set.
	HashSet<Double> finalLcsCandidateSet = getFinalCandidateSet(positiveSet, lcsDistanceNamePairMapping,lcsCandidateSet);

	/*****************************************************************************************************
	 * Comparing the Levenshtein candidate set and LCS candidate set and
	 * producing the final candidate set
	 *****************************************************************************************************/
	
	for (int i = 10; i >= 0; i--) {
		ArrayList<String> namePairHighList = new ArrayList<String>();
		for (int j = i; j >= 0; j--) {
			Double highDistance = new Double(j);
			if (finalLevenshteinCandidateSet.contains(highDistance)
					&& levenshteinDistanceNamePairMapping.containsKey(highDistance)) {
				namePairHighList.addAll(levenshteinDistanceNamePairMapping.get(highDistance));
			}
		}
		ArrayList<String> namePairLowList = new ArrayList<String>();
		for (int k = i - 1; k >= 0; k--) {
			Double lowDistance = new Double(k);
			if (finalLcsCandidateSet.contains(lowDistance) && lcsDistanceNamePairMapping.containsKey(lowDistance)) {
				namePairLowList.addAll(lcsDistanceNamePairMapping.get(lowDistance));
			}
		}
		if (namePairHighList.containsAll(namePairLowList) && namePairHighList.size() == namePairLowList.size()) {
			levenshteinCandidateSet.remove(new Double(i));
		}
	}
	for (int i = 10; i >= 0; i--) {
		ArrayList<String> namePairHighList = new ArrayList<String>();
		for (int j = i; j >= 0; j--) {
			Double highDistance = new Double(j);
			if (finalLcsCandidateSet.contains(highDistance)
					&& lcsDistanceNamePairMapping.containsKey(highDistance)) {
				namePairHighList.addAll(lcsDistanceNamePairMapping.get(highDistance));
			}
		}
		ArrayList<String> namePairLowList = new ArrayList<String>();
		for (int k = i - 1; k >= 0; k--) {
			Double lowDistance = new Double(k);
			if (finalLevenshteinCandidateSet.contains(lowDistance) && levenshteinDistanceNamePairMapping.containsKey(lowDistance)) {
				namePairLowList.addAll(levenshteinDistanceNamePairMapping.get(lowDistance));
			}
		}
		if (namePairHighList.containsAll(namePairLowList) && namePairHighList.size() == namePairLowList.size()) {
			lcsCandidateSet.remove(new Double(i));
		}
	}
	System.out.println("The Levenshtein candidate set is " + finalLevenshteinCandidateSet);
	System.out.println("The Lcs candidate set is " + finalLcsCandidateSet);
   
   }

   /*****************************************************************************************************
	 * Compute the Distance between all possible pairs
	 *****************************************************************************************************/
   
   public static ArrayList<NamePair> calculatePairsDistance(String[] dataSet, String algorithm) {
	if (algorithm.equals("Levenshtein")) {
		ArrayList<NamePair> namePairs = new ArrayList<NamePair>();
		for (int i = 0; i < dataSet.length; i++) {
			for (int j = i + 1; j < dataSet.length; j++) {
				String name = dataSet[i] + "," + dataSet[j];
				double distance = distanceUtil.distance(dataSet[i], dataSet[j]);
				namePairs.add(new NamePair(name, distance));
			}
		}
		return namePairs;
	} else {
		ArrayList<NamePair> namePairs = new ArrayList<NamePair>();
		for (int i = 0; i < dataSet.length; i++) {
			for (int j = i + 1; j < dataSet.length; j++) {
				String name = dataSet[i] + "," + dataSet[j];
				double distance = lcsDistanceUtil.distance(dataSet[i], dataSet[j]);
				namePairs.add(new NamePair(name, distance));
			}
		}
		return namePairs;
	}
   
   }

   /*****************************************************************************************************
	 * Compute the Distance between pairs of strings in positive set
   *****************************************************************************************************/
   
   public static ArrayList<NamePair> calculatePositivePairsDistance(HashSet<String> positiveSet, String algorithm) {
	if (algorithm.equals("Levenshtein")) {
		ArrayList<NamePair> namePairs = new ArrayList<NamePair>();
		Iterator iterator = positiveSet.iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			String firstName = name.split(",")[0];
			String secondName = name.split(",")[1];
			namePairs.add(new NamePair(name, distanceUtil.distance(firstName, secondName)));
		}
		return namePairs;
	} else {
		ArrayList<NamePair> namePairs = new ArrayList<NamePair>();
		Iterator iterator = positiveSet.iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			String firstName = name.split(",")[0];
			String secondName = name.split(",")[1];
			namePairs.add(new NamePair(name, lcsDistanceUtil.distance(firstName, secondName)));
		}
		return namePairs;
	}

   }


   /*****************************************************************************************************
	 * Function to fetch the Dataset
   *****************************************************************************************************/
   
   public static String[] getDataSet() {
	String[] input = {"ShumSelinaWaiSheung","SuhmSelinaWaiSheung","ShuSelinaWaiSheung","ShumSelinaWaiSheng",
			"ShumSelinawiSheung","KeraliHenryG.R.","KeraliHenyG.R.","KeraiHenryG.R.","KeraliHenry","KeraliHenryG.",
			"KeralHenryG.R."};
	return input;
   }

   /*****************************************************************************************************
	 * Returns a Set contating the Positive set
   *****************************************************************************************************/
   
   public static HashSet<String> getPositiveSet() {
	String[] positiveSet = {"ShumSelinaWaiSheung,SuhmSelinaWaiSheung","ShumSelinaWaiSheung,ShuSelinaWaiSheung","ShumSelinaWaiSheung,ShumSelinaWaiSheng","ShumSelinaWaiSheung,ShumSelinawiSheung",
	"KeraliHenryG.R.,KeraliHenyG.R.","KeraliHenryG.R.,KeraiHenryG.R.","KeraliHenryG.R.,KeraliHenry","KeraliHenryG.R.,KeraliHenryG.","KeraliHenryG.R.,KeralHenryG."};
	HashSet<String> s = new HashSet<String>(Arrays.asList(positiveSet));
	return s;
   }

   /*****************************************************************************************************
	 * Stores the mapping of distance and NamePairs in a data structure Example
    * :- 1.0 - [(a,b),(c,d)] , 2.0 - [{e,f),(g,h),(i,j)], etc.,
   *****************************************************************************************************/

   public static HashMap<Double, ArrayList<String>> getDistanceNamePairMapping(ArrayList<NamePair> namePairs) {
	HashMap<Double, ArrayList<String>> distanceNamePairMapping = new HashMap<Double, ArrayList<String>>();
	for (int i = 0; i < namePairs.size(); i++) {
		String name = namePairs.get(i).getName();
		Double distance = namePairs.get(i).getDistance();
		if (distanceNamePairMapping.containsKey(distance)) {
			ArrayList nameList = (ArrayList<String>) distanceNamePairMapping.get(distance);
			nameList.add(name);
			distanceNamePairMapping.put(distance, nameList);
		} else {
			ArrayList<String> nameList = new ArrayList<String>();
			nameList.add(name);
			distanceNamePairMapping.put(distance, nameList);
		}
	}
	return distanceNamePairMapping;
   }

   /*****************************************************************************************************
	 * Returns the candidate set - Theorem-1
   *****************************************************************************************************/

   public static HashSet<Double> getCandidateSet(HashSet<String> positiveSet,HashMap<Double, ArrayList<String>> distanceNamePairMapping) {
	HashSet<Double> candidateSet = new HashSet<Double>();
	for (int i = 10; i >= 0; i--) {
		Double highDistance = new Double(i);
		if (distanceNamePairMapping.containsKey(highDistance)) {
			ArrayList<String> namePairHighList = (ArrayList<String>) distanceNamePairMapping.get(highDistance);
			for (int j = i - 1; j >= 0; j--) {
				Double lowDistance = new Double(j);
				if (distanceNamePairMapping.containsKey(lowDistance)) {
					ArrayList<String> namePairLowList = (ArrayList<String>) distanceNamePairMapping
							.get(lowDistance);
					namePairHighList.removeAll(namePairLowList);
					int count = 0;
					for (int k = 0; k < namePairHighList.size(); k++) {
						if (positiveSet.contains(namePairHighList.get(k))) {
							count++;
						}
					}
					if (count == 0) {
						candidateSet.add(lowDistance);
					}
				}
			}
		}
	}
	return candidateSet;
   
   }

   /*****************************************************************************************************
	 * Returns the candidate set - Theorem-2
   *****************************************************************************************************/

   public static HashSet<Double> getFinalCandidateSet(HashSet<String> positiveSet,
		HashMap<Double, ArrayList<String>> distanceNamePairMapping, HashSet<Double> candidateSet) {
	for (int i = 10; i >= 0; i--) {
		Double highDistance = new Double(i);
		if (distanceNamePairMapping.containsKey(highDistance) && candidateSet.contains(highDistance)) {
			ArrayList<String> namePairHighList = (ArrayList<String>) distanceNamePairMapping.get(highDistance);
			for (int j = i - 1; j >= 0; j--) {
				Double lowDistance = new Double(j);
				if (distanceNamePairMapping.containsKey(lowDistance) && candidateSet.contains(lowDistance)) {
					ArrayList<String> namePairLowList = (ArrayList<String>) distanceNamePairMapping
							.get(lowDistance);
					namePairHighList.removeAll(namePairLowList);
					if (namePairHighList.size() == 0) {
						candidateSet.remove(lowDistance);
					}
				}
			}
		}
	}
	for (int i = 10; i >= 0; i--) {
		Double highDistance = new Double(i);
		if (distanceNamePairMapping.containsKey(highDistance) && candidateSet.contains(highDistance)) {
			ArrayList<String> namePairHighList = (ArrayList<String>) distanceNamePairMapping.get(highDistance);
			for (int j = i - 1; j >= 0; j--) {
				Double lowDistance = new Double(j);
				if (distanceNamePairMapping.containsKey(lowDistance) && candidateSet.contains(lowDistance)) {
					ArrayList<String> namePairLowList = (ArrayList<String>) distanceNamePairMapping
							.get(lowDistance);
					namePairHighList.removeAll(namePairLowList);
					if (positiveSet.containsAll(namePairHighList)) {
						candidateSet.remove(lowDistance);
					}
				}
			}
		}
	}
	return candidateSet;

   }
}
