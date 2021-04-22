package nl.uva.science.esc.search.problems;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Things are categorized into positive numbered bins 0, 1, 2, 3, 4,...
 * Zero may be included as a category, but negative numbers may not.
 * It is not stated beforehand what the range of bins will be and it is
 * not needed that bin-number codes are consequtive.
 * 
 * After the counting phase you can ask for a table of the counts:
 * - all bins
 * - lowest N bins that have a count>0
 * - bins 0 to N-1, including unfilled bins.
 * - count of the remaining bins (after removal of the N lowest)
 * 
 * Reading the lowest N filled bins is destructive: you can only do it once
 * @author kaper
 *
 */
public class IntegerCategoriesSummarizer {
	SortedMap<Integer, Integer> m = new TreeMap<Integer, Integer>();
	int removedCount; //the count of the last removed category
	
	IntegerCategoriesSummarizer(){}
	
	/**
	 * In the counting phase you offer all items in turn to this method
	 * @param category
	 */
	public void addItem(int category) {
		Integer cat = new Integer(category);
		if (m.containsKey(cat)) { //if this category exists already
			m.put(cat, ((int)m.get(cat)+1) );  //increase its counter
		}
		else {
			m.put(cat, 1); //insert new category, with counter=1
		}//end if		
	}//end addItem
	
	/**
	 * Find out the highest category in your set
	 * @return
	 */
	public int highestCategory() {
		return m.lastKey();
	}//end highest
	
	/**
	 * Number of filled categories (nonzero counts)
	 * @return
	 */
	public int size() {
		return m.size();
	}//end size
	
	
	//The following two commands are used to explore fixed ranges
	//like: [0, N-1]
	
	/**
	 * Get the count for a given category, (zero if we didn't hear of it yet)
	 * @param category
	 * @return the count
	 */
	public int getCount(int category) {
		return (m.containsKey(category)) ? ((int)m.get(category)) : (0);
	}//end getCount
	
	/**
	 * Get a total count for a range of categories
	 * @param low, low end of the range, inclusive
	 * @param high, high end of the range, inclusive
	 * @return the count
	 */
	public int addCountsForRange(int low, int high) {
		int count = 0;
		for (int i=low; i<=high; i++) {
			count += getCount(i);
		}//next i
		return count;
	}//end addCountsForRange
	
	
	//Get the lowest N filled bins, regardless in what range they are
	//This reading process is destructive, you can only do it once.
	
	/**
	 * The lowest category is removed and returned
	 * The corresponding count is made available via getRemovedCount
	 * @return category
	 */
	public int removeLowest() {
		Integer key = m.firstKey();
		removedCount = m.get(key);
		m.remove(key);
		return key;
	}//end removeLowest
	
	/**
	 * Get the count correspondng to the category last removed
	 * @return the count
	 */
	public int getRomevedCount() {
		return removedCount;
	}//end getRomevedCount
	
	/**
	 * Get a total count of the still remaining higher categories
	 * @return the count
	 */
	public int removeAndCountRest() {
		int count = 0;
		while (!m.isEmpty()) {
			Integer key = m.firstKey();
			count += m.get(key);
			m.remove(key);
		}//end while
		return count;
	}//end removeAndCountRest

}//end class
