
import java.util.*;
import java.io.*;

public class GrammarDriver {
    public static void main(String[] args) {
        System.out.print("\nMOSQL> ");
        do {
            String input = readInput().trim();
            if (input.equalsIgnoreCase("exit")) {
                break;
            } else {
                // Add the semicolon that readInput leaves off - this is 
                // /important to the grammar!
                input += ";";
            }
            try {
                StringReader reader = new StringReader(input);
                parser p = new parser(new Lexer(reader));
                Relation parse = (Relation)p.parse().value;
            } catch (Exception e) {
                System.out.println(e.toString());
                System.out.println("\nSYNTAX ERROR\n");
            }
        } while (true);
        // We must save database before we exit
        //Relation.saveDatabase("");
        System.out.println("\nGOODBYE!\n");
    }

    private static String readInput() {
        try {
            StringBuffer buff = new StringBuffer();
            System.out.flush();
            int c = System.in.read();
            while (c != ';' && c != -1) {
                if (c != '\n') {
                    buff.append((char)c);
                } else {
                    buff.append(" ");
                    System.out.print("\nMOSQL> ");
                    System.out.flush();
                }
                c = System.in.read();
            }
            return buff.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }
    
    // Conditional select operation
    public static Relation SQLSelect(ArrayList<String> attrList, 
            ArrayList<Condition> condList, ArrayList<String> tblList) {
        for (String tbl : tblList) {
            if (!Relation.tableExists(tbl)) {
                Relation nullRel = new Relation(null, null, null);
                return nullRel;
            }
        }
        // Now we know all specified tables exist
        for (String attr : attrList) {
            if (!Relation.attributeExists(attr, tblList)) {
                // SELECT * is a special case
                if (attr.trim().equals("*") && attrList.size() == 1) {
                    Relation resultRel = null;
                    for (String tbl : tblList) {
                        resultRel = Relation.select(condList, tbl);
                        resultRel.displayRelation();
                    }
                    return resultRel;
                } else {
                    System.err.println("\nError: Attribute " + attr + 
                            " does not exist in specified Relations.\n");
                    Relation nullRel = new Relation(null, null, null);
                    return nullRel;
                }
            }
        }
        // Now we know that all specified attributes exist in one or more 
        // specified tables and that this is NOT the special case of a SELECT *
        // query
        Relation resultRel = null;
        for (String tbl : tblList) {
            resultRel = Relation.select(condList, tbl);
            resultRel = Relation.project(attrList, resultRel);
            resultRel.displayRelation();
        }
        // We'll return the final result Relation, rather arbitrarily
        return resultRel;
    }

    // Select operation for when no conditions are provided
    public static Relation SQLSelect(ArrayList<String> attrList,
            ArrayList<String> tblList) {
        for (String tbl : tblList) {
            if (!Relation.tableExists(tbl)) {
                Relation nullRel = new Relation(null, null, null);
                return nullRel;
            }
        }
        // Now we know all specified tables exist
        for (String attr : attrList) {
            if (!Relation.attributeExists(attr, tblList)) {
                if (attr.trim().equals("*") && attrList.size() == 1) {
                    // SELECT * is a special case
                    Relation resultRel = null;
                    for (String tbl : tblList) {
                        resultRel = Relation.projectAll(tbl);
                        resultRel.displayRelation();
                    }
                    return resultRel;
                } else {
                    System.err.println("\nError: Attribute " + attr + 
                            " does not exist in specified Relations.\n");
                    Relation nullRel = new Relation(null, null, null);
                    return nullRel;
                }
            }
        }
        // Now we know that all specified attributes exist in one or more 
        // specified tables and that this is NOT the special case of a SELECT *
        // query
        Relation resultRel = null;
        for (String tbl : tblList) {
            resultRel = Relation.project(attrList, tbl);
            resultRel.displayRelation();
        }
        // We'll return the final result Relation, rather arbitrarily
        return resultRel;
    }

    public static Relation insertTuple(String tblName, 
            ArrayList<String> attrList, ArrayList<String> valueList) {
        // Make sure table exists
        if (!Relation.tableExists(tblName)) {
            System.out.println("\nError: Table " + tblName + " does not " +
                " exist in the database.\n");
            Relation nullRel = new Relation(null, null, null);
            return nullRel;
        }
        // Make sure specified table contains attributes
        for (String attr : attrList) {
            if (!Relation.containsAttribute(attr, tblName)) {
                System.err.println("\nError: Attribute " + attr + 
                        " does not exist in table " + tblName + ".\n");
                Relation nullRel = new Relation(null, null, null);
                return nullRel;
            }
        }
        // Make sure there are at most as many attributes in the query 
        // as there are attributes in the table specified
        if (!Relation.isAppropriateAttributeNumber(tblName, attrList)) {
            System.out.println("\nError: Number of attributes in" +
                    " table " + tblName + " and insert statement differ.\n");
            Relation nullRel = new Relation(null, null, null);
            return nullRel;
        }
        
        // Make sure number of attributes passed matches number of values passed
        if (!Relation.isAppropriateValueNumber(attrList, valueList)) {
            System.err.println("\nError: Number of attributes does not match " 
                    + "number of values.\n");
        }
        
        if (!Relation.areAppropriateValueTypes(tblName, attrList, valueList)) {
            Relation nullRel = new  Relation(null, null, null);
            return nullRel;
        }
        createAndInsertTuple(tblName, attrList, valueList);
        
        Relation nullRel = new Relation(null, null, null);
        return nullRel;
    }
    
    private static void createAndInsertTuple(String tblName, ArrayList<String> attrList,
             ArrayList<String> valueList) {
        // This is a dummy Relation just to allow us access to the catalog Map
        Relation nullRel = new Relation(null, null, null);
        Relation r = (Relation)nullRel.getCatalogMap().get(tblName);
        int numAttrs = r.getAttributes().size();
        Comparable[] tup = new Comparable[numAttrs];
        for (String attr : r.getAttributes()) {
            int workingAttributeIndex = r.getAttributes().indexOf(attr);
            for (String attrParam : attrList) {
                int workingParamIndex = attrList.indexOf(attrParam);
                if (attr.equalsIgnoreCase(attrParam)) {
                    if (r.getDomains().get(workingAttributeIndex).equals("INTEGER")) {
                        try {
                            int intVal = Integer.parseInt(valueList.get(workingParamIndex));
                            tup[workingAttributeIndex] = intVal;
                        } catch (NumberFormatException nfe) {
                            System.err.println("\nError: Expected type 'INTEGER'" 
                                    + " for parameter " + attrParam + ".\n");
                            return;
                        }
                    } else if (r.getDomains().get(workingAttributeIndex).equals("DECIMAL")) {
                        try {
                            double dVal = Double.parseDouble(valueList.get(workingParamIndex));
                            tup[workingAttributeIndex] = dVal;
                        } catch (NumberFormatException nfe) {
                            System.err.println("\nError: Expected type 'DECIMAL'" +
                                    " for parameter " + attrParam + ".\n");
                            return;
                        }
                    } else {
                        // assume type is VARCHAR
                        tup[workingAttributeIndex] = valueList.get(workingParamIndex);
                    }
                } 
            }
        }
        Tuple t = new Tuple(tup, r.getAttributes(), r.getDomains());
        r.getTable().add(tup);
    }

    public static Relation deleteTuple(String tblName, 
            ArrayList<Condition> conds) {
        // Check that table exists in the database 
        if (!Relation.tableExists(tblName)) {
            System.err.println("\nError: Table: " + tblName + " does not" +
                    " exist in the database.\n");
            Relation nullRel = new Relation(null, null, null);
            return nullRel;
        } 
        // Make sure that each conditional attribute exists in table
        for  (Condition cond : conds) {
            String condAttr = cond.getAttributeName();
            if (!Relation.containsAttribute(condAttr, tblName)) {
                System.err.println("\nError: Table: " + tblName + " does" 
                        + " not contain attribute: " + condAttr + ".\n");
                Relation nullRel = new Relation(null, null, null);
                return nullRel;
            }
        }
        // Make sure there are at most as many conditional attributes as there
        // are attributes in the table
        ArrayList<String> tempConds = new ArrayList<String>();
        ArrayList<String> tempVals = new ArrayList<String>();
        for (Condition cond : conds) {
            String condAttr = cond.getAttributeName();
            String condVal = cond.getAttributeValue();
            tempConds.add(condAttr);
            tempVals.add(condVal);
        }
        if (!Relation.paramNumberDoesNotExceedTableAttributes(tblName, tempConds)) {
            System.err.println("\nError: Number of conditional attrbutes " +
                    "provided exceeds number of attributes in table: " +
                    tblName + ".\n");
            Relation nullRel = new Relation(null, null, null);
            return nullRel;
        }
        // Make sure provided attribute values match attribute types in table
        if (!Relation.areAppropriateValueTypes(tblName, tempConds, tempVals)) {
            Relation nullRel = new Relation(null, null, null);
            return nullRel;
        }
        Relation r = Relation.deleteTuple(tblName, conds);
        r.displayRelation();
        return r;
    }

    public static Relation update(String tblName, ArrayList<Condition> sets, 
            ArrayList<Condition> conds) {
        // First make sure table exists in the database 
        if (!Relation.tableExists(tblName)) {
            System.err.println("\nError: Table: " + tblName + " does not " +
                    "exist in the database.\n");
            Relation nullRel = new Relation(null, null, null);
            return nullRel;
        }
        // Make sure each set attribute exists in the table
        for (Condition set : sets) {
            String setName = set.getAttributeName();
            if (!Relation.containsAttribute(setName, tblName)) {
                System.err.append("\nError: Attribute: " + setName + " does " +
                        "not exist in table: " + tblName + ".\n");
                Relation nullRel = new Relation(null, null, null);
                return nullRel;
            }
        }
        
        // Make sure every sets condition is an equivalence condition - i.e. that 
        // we are in fact *setting* some attriubute to some value
        for (Condition set : sets) {
            String comp = set.getComparison();
            if (!comp.equals("=")) {
                System.err.println("\nError: Set condition: " + set + " is " +
                        "not an equivalence.\n");
                Relation nullRel = new Relation(null, null, null);
                return nullRel;
            }
        }
        
        // Make sure each conditional attribute exists in the table
        for (Condition cond : conds) {
            String attrName = cond.getAttributeName();
            if (!Relation.containsAttribute(attrName, tblName)) {
                System.err.println("\nError: Attribute " + attrName + " does " 
                        + "not exist in table: " + tblName + ".\n");
                Relation nullRel = new Relation(null, null, null);
                return nullRel;
            }
        }
        Relation r = Relation.updateTuple(tblName, sets, conds);
        r.displayRelation();
        return r;
    }
}
