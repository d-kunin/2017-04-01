package ru.otus.kunin.dorm.server;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author v.chibrikov
 */
class TemplateProcessor {

  private static final String HTML_DIR = "tml";

  private static final Logger LOG = LoggerFactory.getLogger(TemplateProcessor.class);

  private static TemplateProcessor instance = new TemplateProcessor();

  private final Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);

  private TemplateProcessor() {
  }

  static TemplateProcessor instance() {
    return instance;
  }

  String getPage(String filename, Map<String, Object> data) {
    try (Writer stream = new StringWriter()) {
      Template template = configuration.getTemplate(HTML_DIR + File.separator + filename);
      template.process(data, stream);
      return stream.toString();
    } catch (TemplateException | IOException e) {
      LOG.error("Error processing template", e);
      throw new RuntimeException(e);
    }
  }
}
