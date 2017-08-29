package com.geronimostudios.drive2android.core;


import com.google.api.client.http.ByteArrayContent;
import com.google.common.collect.Iterables;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringEscapeUtils;
import com.geronimostudios.drive2android.Drive2AndroidExtension;
import com.geronimostudios.drive2android.model.Header;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParseHelper {
    private static final String ROOT_ELEMENT = "resources";
    private static final String STRING_ELEMENT = "string";
    private static final String KEY_STRING = "name";
    private static final String KEY_OPTION = "formatted";

    private static Header mCSVHeader;
    private static Header mXMLHeader;

    public static HashMap<String, String[]> CSVtoMap(ByteArrayOutputStream file) throws IOException {
        int len;
        String key;
        String[] values;
        HashMap<String, String[]> map;
        Reader reader;
        Iterable<CSVRecord> records;
        CSVRecord header;

        map = new HashMap<>();
        reader = new InputStreamReader(new ByteArrayInputStream(file.toByteArray()));
        records = CSVFormat.RFC4180.parse(reader);
        header = Iterables.get(records, 0);
        len = header.size() - 1;
        mCSVHeader = parseHeader(header);
        for (CSVRecord record : records) {
            key = record.get(0);
            values = new String[len];
            for (int i = 0; i < len; i++)
                values[i] = record.get(i + 1);
            map.put(key, values);
        }
        return map;
    }

    public static HashMap<String, String[]> XMLtoMAP(File[] fileList) throws IOException, JDOMException {
        String key;
        String[] values;
        Document doc;
        Element racine;
        Iterable<Element> strings;
        String folder;

        HashMap<String, String[]> map = new HashMap<>();
        SAXBuilder sxb = new SAXBuilder();
        if (mCSVHeader == null) {
            mXMLHeader = parseHeader(fileList);
        } else {
            mXMLHeader = mCSVHeader.add(filesToStrings(fileList));
        }
        int langNumber = mXMLHeader.size();
        for (File f : fileList) {
            doc = sxb.build(f);
            racine = doc.getRootElement();
            strings = racine.getChildren("string");
            folder = f.getParentFile().getName();
            for (Element string : strings) {
                values = map.get(string.getAttributeValue(KEY_STRING));
                if (values == null) {
                    values = new String[langNumber];
                    key = string.getAttributeValue(KEY_STRING);
                    values[mXMLHeader.getIndex(folder)] = string.getValue();
                    map.put(key, values);
                } else {
                    values[mXMLHeader.getIndex(folder)] = string.getValue();
                }
            }
        }
        echoMap(map);
        return map;
    }

    private static String[] filesToStrings(File[] filesList) {
        String[] res;
        int cpt;

        res = new String[filesList.length];
        cpt = 0;
        for (File f : filesList) {
            res[cpt] = f.getParentFile().getName();
            cpt++;
        }
        return res;
    }

    private static Header parseHeader(File[] fileList) {
        return new Header(filesToStrings(fileList));
    }

    private static String[] csvRecordsToStrings(CSVRecord record) {
        String[] res;

        res = new String[record.size() - 1];
        for (int cpt = 0; cpt < record.size() - 1; cpt++) {
            if (record.get(cpt + 1).contains(File.separator)) {
                throw new SecurityException("Invalid file name : " + record.get(cpt + 1));
            }
            res[cpt] = record.get(cpt + 1);
        }
        return res;
    }

    private static Header parseHeader(CSVRecord csv) {
        return new Header(csvRecordsToStrings(csv));
    }

    public static void write(HashMap<String, String[]> map, Drive2AndroidExtension d2a)
            throws IOException
    {
        Element tmp;
        Attribute att;
        File output;
        String value;
        String path;
        Format format;
        XMLOutputter writer;
        Document[] docs;

        format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        writer = new XMLOutputter(format);
        docs = new Document[mCSVHeader.size()];
        for (int i = 0; i < docs.length; i++) {
            tmp = new Element(ROOT_ELEMENT);
            docs[i] = new Document(tmp);
        }
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue().length; i++) {
                value = entry.getValue()[i];
                if (value != null && !value.isEmpty()) {
                    tmp = new Element(STRING_ELEMENT);
                    tmp.setText(entry.getValue()[i]);
                    att = new Attribute(KEY_STRING, entry.getKey());
                    tmp.setAttribute(att);
                    if (value.contains("%")) {
                        att = new Attribute(KEY_OPTION, "false");
                        tmp.setAttribute(att);
                    }
                    docs[i].getRootElement().addContent(tmp);
                }

            }
        }
        for (int i = 0; i < docs.length; i++) {
            path = d2a.getResPath()
                    + File.separator + mCSVHeader.getValue(i);
            output = new File(path);
            output.mkdirs();
            output = new File(path + File.separator + d2a.getStringFileName());
            writer.output(docs[i], new FileOutputStream(output, false));
        }
    }

    public static void echoMap(HashMap<String, String[]> map) {
        System.out.println(mapFormat(map));
    }


    public static File[] getStringFiles(Drive2AndroidExtension d2a) throws IOException {
        File[] res;
        File[] files;
        File[] subfolder;
        ArrayList<File> tmp;
        File folder;

        tmp = new ArrayList<>();
        folder = new File(d2a.getResPath());
        subfolder = folder.listFiles((dir, name) -> name.contains("values"));
        if (subfolder == null || subfolder.length == 0) {
            return null;
        }
        System.out.println(subfolder.length);
        for (File f : subfolder) {
            files = f.listFiles((dir, name) -> name.equals(d2a.getStringFileName()));
            if (files != null && files.length != 0) {
                tmp.add(files[0]);
            }
        }
        res = new File[tmp.size()];
        return tmp.toArray(res);
    }

    @SuppressWarnings("deprecation")
    private static String mapFormat(HashMap<String, String[]> map) {
        int len;
        String res;

        res = "";
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            res += StringEscapeUtils.escapeCsv(entry.getKey()) + ",";
            len = entry.getValue().length - 1;
            for (int i = 0; i < len; i++) {
                res += (entry.getValue()[i] != null) ?
                        StringEscapeUtils.escapeCsv(entry.getValue()[i]) + "," : ",";
            }
            res += (entry.getValue()[len] != null) ?
                    StringEscapeUtils.escapeCsv(entry.getValue()[len]) + "\n" : "\n";
        }
        return res;
    }

    public static void upload(HashMap<String, String[]> map, Drive2AndroidExtension d2a)
            throws IOException
    {
        byte[] filePath;
        String out;
        ByteArrayContent mediaContent;
        com.google.api.services.drive.model.File fileMetadata;

        fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
        out = mCSVHeader.format();
        out += mapFormat(map);
        System.out.println(out);
        filePath = out.getBytes("UTF-8");
        mediaContent = new ByteArrayContent("text/csv", filePath);
        d2a.getDrive().files().update(d2a.getFileId(), fileMetadata, mediaContent).execute();
    }
}
