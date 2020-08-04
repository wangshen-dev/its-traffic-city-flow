package its.traffic.flow.utils;


import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


public class SortUtils {
	/**
	 * TreeMap的排序方式，按Value升序排序
	 */
	public static void main(String[] args) {
		Map<String, Integer> map = new TreeMap<String, Integer>();
		map.put("acb1", 5);
		map.put("bac1", 1);
		map.put("bca1", 60);
		map.put("cab1", 80);
		map.put("cba1", 1);
		map.put("abc1", 60);
		map.put("abc2", 60);
		zsvalueUpSort(map);
	}

	/*
	 * map根据value排序
	 */
	public static List<Entry<String, Integer>> zsvalueUpSort(Map<String,Integer> map_final) {


		// 升序比较器
		Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
		};
		// map转换成list进行排序
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(map_final.entrySet());
		// 排序
		Collections.sort(list, valueComparator);

		// 默认情况下，TreeMap对key进行升序排序
//		System.out.println("------------map按照value降序排序--------------------");


//		for (int i = list.size() - 1; i >= 0; i--) {
//			System.out.println(list.get(i).getKey()+"--"+list.get(i).getValue());
//		}
//		

		return list;


	}


	/*
	 * map根据value排序
	 */
	public static List<Entry<String, Integer>> mapDescSort(Map<String,Integer> map_final) {


		// 升序比较器
		Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
		};
		// map转换成list进行排序
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(map_final.entrySet());
		// 排序
		Collections.sort(list, valueComparator);

		// 默认情况下，TreeMap对key进行升序排序

//		for (int i = list.size() - 1; i >= 0; i--) {
//			System.out.println(list.get(i).getKey()+"--"+list.get(i).getValue());
//		}

//		List<Entry<String, Integer>> list2 = new ArrayList<Entry<String,Integer>>();
//		System.out.println("------------map按照value降序排序--------------------");
//		  for(int i =0 ;i<=list.size()-1; i++) {
//			  System.out.println(list.get(i).getKey()+"--"+list.get(i).getValue());
//
//		  }

		return list;


	}

	/*
	 * map根据value排序
	 */
	public static List<Entry<String, Double>> mapDoubleDescSort(Map<String,Double> map_final) {


		// 升序比较器
		Comparator<Entry<String, Double>> valueComparator = new Comparator<Entry<String, Double>>() {
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return (int) (o1.getValue() - o2.getValue());
			}
		};
		// map转换成list进行排序
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(map_final.entrySet());
		// 排序
		Collections.sort(list, valueComparator);


		return list;


	}


	/**
	 * 排序
	 * @param aMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortMapByValues(Map<K, V> aMap) {
		HashMap<K, V> finalOut = new LinkedHashMap<>();
		aMap.entrySet()
				.stream()
				.sorted((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
				.collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
		return finalOut;
	}



	/**
	 * 排序
	 * @param aMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortMapByKeys(Map<K, V> aMap) {
		HashMap<K, V> finalOut = new LinkedHashMap<>();
		aMap.entrySet()
				.stream()
				.sorted((p1, p2) -> p1.getKey().compareTo(p2.getKey()))
				.collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
		return finalOut;
	}


	/**
	 * set 排序
	 * @param set
	 */
	public static void sortSet(Set<Integer> set) {
		set = new TreeSet<>(new Comparator<Integer>() {
			public int compare(Integer arg0, Integer arg1) {
				return arg0.compareTo(arg1);
			};
		});
	}
}
