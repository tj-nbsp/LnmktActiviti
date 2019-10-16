package com.jiajin.lnmkt.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("service")
public class EditorController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    RepositoryService repositoryService;
    @Resource
    ObjectMapper objectMapper;

    /**
     * 加载用于渲染流程组件的 JSON 配置
     */
    @RequestMapping(value = "/editor/stencilset")
    public String loadStencilset() throws IOException {
        InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("static/stencilset.json");
        return IOUtils.toString(stencilsetStream, "utf-8");
    }

    /**
     * 加载 Model 信息
     */
    @RequestMapping(value = "/model/{modelId}/json")
    public ObjectNode loadModel(@PathVariable("modelId") String modelId) {
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
                } else {
                    modelNode = objectMapper.createObjectNode();
                    modelNode.put(ModelDataJsonConstants.MODEL_NAME, model.getName());
                }
                modelNode.put(ModelDataJsonConstants.MODEL_ID, model.getId());
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
                modelNode.putPOJO("model", editorJsonNode);
            } catch (Exception e) {
                log.error("Error creating model JSON", e);
                throw new ActivitiException("Error creating model JSON", e);
            }
        }
        return modelNode;
    }

    /**
     * 保存流程
     * 
     * @param modelId 模型ID
     * @param name 流程模型名称
     * @param description
     * @param json_xml 流程文件
     * @param svg_xml 图片
     */
    @RequestMapping(value = "/model/{modelId}/save")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveModel(@PathVariable String modelId, String name, String description, String json_xml, String svg_xml) {
        try(ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            Model model = repositoryService.getModel(modelId);

            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            modelJson.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            
            model.setName(name);
            model.setMetaInfo(modelJson.toString());
            repositoryService.saveModel(model);

            repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));

            // Do the transformation
            PNGTranscoder transcoder = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svg_xml.getBytes("utf-8")));
            TranscoderOutput output = new TranscoderOutput(outStream);
            transcoder.transcode(input, output);
            repositoryService.addModelEditorSourceExtra(model.getId(), outStream.toByteArray());
        } catch (Exception e) {
            log.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }
    }

}
