/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import org.gradle.api.GradleException
import org.gradle.api.plugins.quality.Checkstyle

fun reportErrorStyle(checkstyle: Checkstyle) {
    val reportXml = XmlParser().parse(checkstyle.reports.xml.outputLocation.asFile.get())
    val files = reportXml.value() as NodeList
    val errorMessages = mutableListOf<String>()
    files.forEach {
        it as Node
        val filePath = it.attribute("name") as String
        val errors = it.value() as NodeList
        errors.forEach {
            val attributes = (it as Node).attributes()
            errorMessages.add("""
                原因: ${attributes["message"]}
                   请点击跳转进行修复: $filePath:${attributes["line"]}:${attributes["column"]}
            """.trimIndent())
        }
    }
    if (errorMessages.size > 0) {
        val result = StringBuilder("中止编译, 发现影响代码质量的缺陷")
        for ((index, errorMessage) in errorMessages.withIndex()) {
            result.append("\n第${index + 1}个: $errorMessage")
        }
        throw GradleException(result.toString())
    }
}