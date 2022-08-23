package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ConditionStack {

    public ConditionStack(){

    }

    public ArrayList<Row> evaluate(List<Condition> conditions){
        Stack<Condition> s = new Stack<>();
        ArrayList<Condition> expression = new ArrayList<>();
        for (Condition condition : conditions){
            if (!condition.getType().equals("RBRACKET")){
                s.push(condition);
            }
            else {
                while (!s.peek().getType().equals("LBRACKET")){
                    expression.add(s.pop());
                }
                if (s.peek().getType().equals("LBRACKET")){
                    s.pop();
                }
                if (expression.size() == 1){
                    s.push(expression.get(0));
                    expression.clear();
                }
                if (expression.size()==3) {
                    Condition res = new Condition("RESULT");
                    res.setResult(evaluateExpression(expression));
                    s.push(res);
                    expression.clear();
                }
            }

        }
        expression.clear();
        while (!s.isEmpty()){
            expression.add(s.pop());
        }
        return evaluateExpression(expression);
    }

    public ArrayList<Row> evaluateExpression(ArrayList<Condition> expression){
        if (expression.get(1).getOperator().equals("OR")){
            return evaluateOr(expression.get(0).getResult(), expression.get(2).getResult());
        }
        return evaluateAnd(expression.get(0).getResult(), expression.get(2).getResult());

    }

    public ArrayList<Row> evaluateOr(ArrayList<Row> setOne, ArrayList<Row> setTwo){
        ArrayList<Row> result = new ArrayList<>();
        if (setOne==null || setTwo == null){
            return null;
        }
        for (Row i : setOne){
            boolean isDuplicate = false;
            for (Row j : setTwo){
                if (i.toString().equals(j.toString())){
                    isDuplicate = true;
                }
            }
            if (!isDuplicate){
                result.add(i);
            }
        }
        result.addAll(setTwo);
        return result;
    }

    public ArrayList<Row> evaluateAnd(ArrayList<Row> setOne, ArrayList<Row> setTwo){
        ArrayList<Row> result = new ArrayList<>();
        for (Row i : setOne){
            for (Row j : setTwo){
                if (i.toString().equals(j.toString())){
                    result.add(i);
                }
            }
        }
        return result;
    }

}
