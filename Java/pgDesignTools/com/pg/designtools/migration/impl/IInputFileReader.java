package com.pg.designtools.migration.impl;

import java.io.IOException;

import com.matrixone.apps.domain.util.MapList;

public interface IInputFileReader {

	public MapList readContents(String strInputFilePath) throws IOException;
}
