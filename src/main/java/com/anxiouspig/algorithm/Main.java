package com.anxiouspig.algorithm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.*;

public class Main {

    /**
     * @param logic 0：或； 1：与； 3：无。
     * @param value 事件。
     * @param list 底层事件。
     */
    private static final String json = "{\"logic\":0,\"list\":[{\"logic\":0,\"list\":[{\"logic\":1,\"list\":[{\"logic\":3,\"list\":null,\"value\":\"1\"},{\"logic\":3,\"list\":null,\"value\":\"2\"},{\"logic\":3,\"list\":null,\"value\":\"3\"}],\"value\":\"E3\"},{\"logic\":1,\"list\":[{\"logic\":3,\"list\":null,\"value\":\"3\"},{\"logic\":3,\"list\":null,\"value\":\"4\"}],\"value\":\"E4\"}],\"value\":\"E1\"},{\"logic\":1,\"list\":[{\"logic\":0,\"list\":[{\"logic\":3,\"list\":null,\"value\":\"4\"},{\"logic\":3,\"list\":null,\"value\":\"6\"}],\"value\":\"E5\"},{\"logic\":0,\"list\":[{\"logic\":3,\"list\":null,\"value\":\"5\"},{\"logic\":3,\"list\":null,\"value\":\"6\"}],\"value\":\"E6\"}],\"value\":\"E2\"}],\"value\":\"T\"}";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static List<CutSet> setlist = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        JsonNode jsonNode = mapper.readTree(json);
        recursion(null, jsonNode);
        System.out.println(uniq());
    }

    private static List<List<Integer>> uniq() {
        List<List<Integer>> lists = new ArrayList<>();
        List<List<Integer>> sets = new ArrayList<>();
        setlist.forEach(set -> set.getList().forEach(l -> lists.add(l)));
        lists.forEach(s -> {
            List<Integer> l = new ArrayList<>(new HashSet<>(s));
            Collections.sort(l);
            sets.add(l);
        });
        List<List<Integer>> res = new ArrayList<>(sets);
        for (int i = 0; i < sets.size(); i++) {
            List<Integer> listI = sets.get(i);
            for (int j = sets.size() - 1; j > i; j--) {
                List<Integer> listJ = sets.get(j);
                int iMax = listI.get(listI.size() - 1);
                int iMin = listI.get(0);
                int jMax = listJ.get(listJ.size() - 1);
                int jMin = listJ.get(0);
                if (iMax > jMax - 1 && iMin < jMin + 1) {
                    res.remove(listI);
                    continue;
                } else if (iMax < jMax + 1 && iMin > jMin - 1) {
                    res.remove(listJ);
                    continue;
                }
            }
        }
        return res;
    }

    private static void recursion(String parent, JsonNode node) {
        if (node.get("logic").asInt() == 3) {
            setlist.add(new CutSet(parent, Arrays.asList(Arrays.asList(node.get("value").asInt()))));
            return;
        }
        String value = node.get("value").asText();
        Iterator<JsonNode> iterator = node.get("list").iterator();
        while (iterator.hasNext()) {
            JsonNode n = iterator.next();
            recursion(value, n);
        }
        int logic = node.get("logic").asInt();
        Iterator<CutSet> i = setlist.iterator();
        if (logic == 0) {
            List<List<Integer>> l = new ArrayList<>();
            while (i.hasNext()) {
                CutSet child = i.next();
                String p = child.getParent();
                if (p.equals(value)) {
                    i.remove();
                    child.getList().forEach(t -> {
                        l.add(t);
                    });
                }
            }
            setlist.add(new CutSet(parent, l));
        } else if (logic == 1) {
            List<CutSet> sets = new ArrayList<>();
            while (i.hasNext()) {
                List<List<Integer>> l = new ArrayList<>();
                CutSet child = i.next();
                String p = child.getParent();
                if (p.equals(value)) {
                    i.remove();
                    sets.add(child);
                }
            }
            getCombinationRecursion(parent, 0, new ArrayList<>(), sets);
        }
    }

    private static void getCombinationRecursion(String parent, int index, List<Integer> list, List<CutSet> sets) {
        if (sets.size() == 1) {
            sets.get(0).getList().forEach(i -> {
                List<Integer> result = new ArrayList<>(list);
                result.addAll(i);
                setlist.add(new CutSet(parent, Arrays.asList(result)));
            });
            return;
        }
        List<CutSet> remain = new ArrayList<>(sets);
        CutSet set = sets.get(index);
        remain.remove(set);
        set.getList().forEach(c1 -> {
            List<Integer> result = new ArrayList<>(list);
            result.addAll(c1);
            getCombinationRecursion(parent, index + 1, result, remain);
        });
    }

    @Data
    private static class CutSet {
        final String parent;
        final List<List<Integer>> list;
    }


//    public static void main(String[] args) {
//        List<CutSet> sets = new ArrayList<>();
//        sets.add(new CutSet("1", Arrays.asList(Arrays.asList(1))));
//        sets.add(new CutSet("1", Arrays.asList(Arrays.asList(2))));
//        sets.add(new CutSet("1", Arrays.asList(Arrays.asList(3))));
//        getCombinationRecursion("1", 0, new ArrayList<>(), sets);
//        System.out.println(setlist);
//    }


//    public static void main(String[] args) throws Exception {


//        Map<String, Object> map = getMap("T", 0,
//                Arrays.asList(
//                        getMap("E1", 0,
//                                Arrays.asList(
//                                        getMap("E3", 1,
//                                                Arrays.asList(
//                                                        getMap("1", 3, null),
//                                                        getMap("2", 3, null),
//                                                        getMap("3", 3, null)
//                                                )),
//                                        getMap("E4", 1,
//                                                Arrays.asList(
//                                                        getMap("3", 3, null),
//                                                        getMap("4", 3, null)
//                                                ))
//                                )),
//                        getMap("E2", 1,
//                                Arrays.asList(
//                                        getMap("E5", 0,
//                                                Arrays.asList(
//                                                        getMap("4", 3, null),
//                                                        getMap("6", 3, null)
//                                                )),
//                                        getMap("E6", 0,
//                                                Arrays.asList(
//                                                        getMap("5", 3, null),
//                                                        getMap("6", 3, null)
//                                                ))
//                                ))
//                ));
//
//                ObjectMapper mapper = new ObjectMapper();
//        System.out.println(mapper.writeValueAsString(map));


//    }

//    static Map<String, Object> getMap(String value, int logic, List<Map<String, Object>> list){
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("value", value);
//        map.put("logic", logic);
//        map.put("list", list);
//        return map;
//    }
}
