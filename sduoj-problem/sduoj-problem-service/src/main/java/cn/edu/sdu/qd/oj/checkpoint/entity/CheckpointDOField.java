/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.checkpoint.entity;

public class CheckpointDOField {
    public static final String TABLE_NAME = "oj_checkpoint";
    public static final String ID = "c_id";
    public static final String GMT_CREATE = "c_gmt_create";
    public static final String GMT_MODIFIED = "c_gmt_modified";
    public static final String features = "c_features";
    public static final String INPUT_PREVIEW = "c_input_preview";
    public static final String OUTPUT_PREVIEW = "c_output_preview";
    public static final String INPUT_SIZE = "c_input_size";
    public static final String OUTPUT_SIZE = "c_output_size";
    public static final String INPUT_FILE_NAME = "c_input_file_name";
    public static final String OUTPUT_FILE_NAME = "c_output_file_name";
    public static final String INPUT_FILE_ID = "c_input_file_id";
    public static final String OUTPUT_FILE_ID = "c_output_file_id";
}