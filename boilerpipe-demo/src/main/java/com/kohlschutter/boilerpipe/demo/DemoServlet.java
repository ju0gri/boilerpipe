/**
 * boilerpipe
 * <p>
 * Copyright (c) 2009, 2014 Christian Kohlschütter
 * <p>
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kohlschutter.boilerpipe.demo;

import com.google.gson.Gson;
import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.document.Image;
import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.sax.BoilerpipeSAXInput;
import com.kohlschutter.boilerpipe.sax.HTMLFetcher;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;
import com.kohlschutter.boilerpipe.sax.ImageExtractor;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DemoServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String urlStr = req.getParameter("url");
        String extractorName = req.getParameter("extractor");
        String outputType = req.getParameter("output");

        if ((urlStr == null || urlStr.isEmpty()) || (extractorName == null || extractorName.isEmpty())
                || (outputType == null || outputType.isEmpty())) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Url, extractor or output parameter is missing!");
            return;
        }

//    String extractImages = req.getParameter("extractImages");
//    String token = req.getParameter("token");

        try {
            URL url;
            url = new URL(urlStr);

            final InputSource is = HTMLFetcher.fetch(url).toInputSource();
            final BoilerpipeSAXInput in = new BoilerpipeSAXInput(is);
            final TextDocument doc = in.getTextDocument();

            // You have the choice between different Extractors
            BoilerpipeExtractor extractor = null;
            switch (extractorName) {
                case "DefaultExtractor":
                    extractor = CommonExtractors.DEFAULT_EXTRACTOR;
                    break;
                case "ArticleExtractor":
                    extractor = CommonExtractors.ARTICLE_EXTRACTOR;
                    break;
                case "CanolaExtractor":
                    extractor = CommonExtractors.CANOLA_EXTRACTOR;
                    break;
                case "LargestContentExtractor":
                    extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;
                    break;
            }

            if (extractor == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid extractor type!");
                return;
            }
            Gson gson = new Gson();

            switch (outputType) {
                case "json":
                    resp.setContentType("application/json");
                    try {
                        ExtractedText response = new ExtractedText(doc.getTitle(), urlStr, extractor.getText(doc));
                        Status status = new Status();
                        status.setSuccess(true);
                        status.setDescription("success");
                        status.setResponse(response);
                        resp.getWriter().println(gson.toJson(status));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Status status = new Status();
                        status.setSuccess(false);
                        status.setDescription(ex.getMessage());
                        resp.getWriter().println(gson.toJson(status));
                    }

                    break;
                case "text":
                    resp.setContentType("text/plain");
                    resp.getWriter().println(extractor.getText(doc));
                    break;
                case "debug":// debug output
                    resp.setContentType("text/plain");
                    extractor.process(doc);
                    resp.getWriter().println(extractor.getText(doc));
                    break;
                case "htmlFragment": // extract fragment
                    HTMLHighlighter hhf = HTMLHighlighter.newHighlightingInstance();
                    resp.setContentType("text/html");
                    resp.getWriter().println(hhf.process(url, extractor));
                    break;
                case "html": // highlight
                    HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
                    resp.setContentType("text/html");
                    resp.getWriter().println(hh.process(url, extractor));
                    break;
                case "img":
                    ImageExtractor ie = ImageExtractor.INSTANCE;
                    resp.setContentType("application/json");
                    Status status = new Status();
                    status.setSuccess(true);
                    status.setDescription("success");

                    List<Image> imgUrls = ie.process(url, extractor);

                    Collections.sort(imgUrls);

                    List<String> imgSrcs = new ArrayList<String>();
                    for (Image img : imgUrls) {
                        imgSrcs.add(img.getSrc());
                    }

                    ExtractedImages images = new ExtractedImages(doc.getTitle(), urlStr, imgSrcs);
                    status.setResponse(images);

                    resp.getWriter().println(gson.toJson(status));
                    break;
            }

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
