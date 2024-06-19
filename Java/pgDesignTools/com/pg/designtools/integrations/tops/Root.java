package com.pg.designtools.integrations.tops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("root")
public class Root {
	@XStreamAlias("CustomFlag")
	private String customflag;
	@XStreamAlias("version")
	private String version;
	@XStreamAlias("info")
	private RootInfo info;
	@XStreamAlias("input")
	private Input input;
	@XStreamAlias("output")
	private Output output;

	public Root() {
		super();
	}

	public Root(String customFlag, String version) {
		super();
		customflag = customFlag;
		this.version = version;
	}

	public String getCustomFlag() {
		return customflag;
	}

	public void setCustomFlag(String customFlag) {
		this.customflag = customFlag;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public RootInfo getInfo() {
		return info;
	}

	public void setInfo(RootInfo info) {
		this.info = info;
	}

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public Output getOutput() {
		return output;
	}

	public void setOutput(Output output) {
		this.output = output;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + customflag + ", " + version + ", " + info + ", " + input + ", "
				+ output + "]";
	}

	public List<Object> getAllElements() {
		List<Object> allEl = new ArrayList<>();

		if (this.customflag != null) {
			allEl.add("CustomFlag");
		}
		if (this.version != null) {
			allEl.add("version");
		}
		if (this.info != null) {
			allEl.add("info");
		}
		if (this.input != null) {
			allEl.add(this.input.getClass().getName());
		}

		return allEl;
	}

	public Map<String, Object> getAllElementsMap() {
		Map<String, Object> allEl = new HashMap<>();

		if (this.customflag != null) {
			allEl.put("CustomFlag", this.customflag);
		}
		if (this.version != null) {
			allEl.put("version", this.version);
		}
		if (this.info != null) {
			allEl.put("info", this.info);
		}

		if (this.input != null) {
			allEl.put(this.input.getClass().getName(), this.input);
		}
		return allEl;
	}
}
