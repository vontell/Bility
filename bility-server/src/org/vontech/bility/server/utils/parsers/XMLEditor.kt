package org.vontech.bility.server.utils.parsers

import kotlinx.html.dom.create
import org.vontech.bility.server.logger
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.TransformerFactory

class XMLEditor(private var filePath: String) {

    private var doc: Document

    init {
        val docFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docFactory.newDocumentBuilder()
        doc = docBuilder.parse(filePath)
    }

    fun insertPermissionIntoManifest(permission: String) {

        logger?.info(doc.textContent)

        val manifest = doc.getElementsByTagName("manifest").item(0)
        logger?.info(manifest.attributes.toString())
        val permissions = mutableListOf<String>()
        val children = manifest.childNodes
        for (i in 0..(children.length-1)) {
            val node = children.item(i)
            if (node.nodeName == "uses-permission") {
                permissions.add(node.attributes.getNamedItem("android:name").textContent)
            }
        }

        // If permission not included, add it to the manifest
        if (!permissions.contains(permission)) {
            val permissionElement = doc.createElement("uses-permission")
            permissionElement.setAttribute("android:name", permission)
            manifest.appendChild(permissionElement)
        }

        logger?.info(permissions.toString())

    }

    fun save() {
        // write the content into xml file
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(doc)
        val result = StreamResult(File(filePath))
        transformer.transform(source, result)
    }

}