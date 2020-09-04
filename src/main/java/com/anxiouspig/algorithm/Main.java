package com.anxiouspig.algorithm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;
import java.util.*;

public class Main {

    /**
     * @param logic 0：或； 1：与； 3：无。
     * @param value 事件。
     * @param list 底层事件。
     */
    private static  String json;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static List<CutSet> setlist = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(Main.class.getClassLoader().getResourceAsStream("json.yml"), Map.class);
        json = mapper.writeValueAsString(map);
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
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < sets.size(); i++) {
            List<Integer> listI = sets.get(i);
            for (int j = sets.size() - 1; j > i; j--) {
                List<Integer> listJ = sets.get(j);
                if (listJ.containsAll(listI)) {
                    sets.remove(listJ);
                    continue;
                } else if (listI.containsAll(listJ)) {
                    res.add(listI);
                    break;
                }
            }
        }
        sets.removeAll(res);
        return sets;
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
            List<List<Integer>> lists = new ArrayList<>();
            getCombinationRecursion(new ArrayList<>(), sets, lists);
            setlist.add(new CutSet(parent, lists));
        }
    }

    private static void getCombinationRecursion(List<Integer> list, List<CutSet> sets, List<List<Integer>> lists) {
        if (sets.size() == 1) {
            sets.get(0).getList().forEach(i -> {
                List<Integer> result = new ArrayList<>(list);
                result.addAll(i);
                lists.add(result);
            });
            return;
        }
        List<CutSet> remain = new ArrayList<>(sets);
        CutSet set = sets.get(0);
        remain.remove(set);
        set.getList().forEach(c1 -> {
            List<Integer> result = new ArrayList<>(list);
            result.addAll(c1);
            getCombinationRecursion(result, remain, lists);
        });
    }

    @Data
    private static class CutSet {
        final String parent;
        final List<List<Integer>> list;
    }

}
