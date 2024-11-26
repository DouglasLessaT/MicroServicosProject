package com.servicemain.servicemain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTask {

    private List<Object> tasks =  new ArrayList<>();

    public void add(Object task){
        tasks.add(task);
    }

    public Object remove(Object task){
        if (tasks.contains(task)) {
            tasks.remove(task);
            return task;
        }
      return null;
    }

}
