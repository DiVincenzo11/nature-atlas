package com.keshihua.ksh.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface BirdCharService {
    ArrayList<ArrayList<Object>> allChardata(String mouth, String crest, String neck, String back, String leg);
}
