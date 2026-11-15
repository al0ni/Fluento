package com.fluento.review;

import com.fluento.model.Exercise;

import java.util.List;
import java.util.Map;

public interface ReviewStrategy {
    String name();

    
    List<Exercise> order(List<Exercise> exercises, Map<String, Integer> mistakes);
}
