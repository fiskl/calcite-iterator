package com.github.fiskl.calcite.database.tables;

import java.util.Map;

public interface DataReciever {

	void populate(String id, Map<String, Object> data);

}