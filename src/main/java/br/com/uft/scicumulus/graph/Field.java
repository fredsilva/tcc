/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import br.com.uft.scicumulus.enums.FieldType;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Frederico da Silva Santos
 *
 */
public class Field {
    String name;
    FieldType fieldType;
    Relation input, output;
    Map<String, String> map = new HashMap<>();            
}
