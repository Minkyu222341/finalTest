package sparta.seed.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileImpl implements MultipartFile {
	private String fileName;
	private String originalFilename;
	private String contentType;
	private boolean empty;
	private long size;
	private byte[] bytes;
	private InputStream inputStream;

	public MultipartFileImpl(String fileName, byte[] bytes, MultipartFile originalImage) throws IOException {
		this.fileName = fileName;
		this.originalFilename = originalImage.getOriginalFilename();
		this.contentType = originalImage.getContentType();
		this.empty = originalImage.isEmpty();
		this.size = originalImage.getSize();
		this.bytes = bytes;
		this.inputStream = originalImage.getInputStream();
	}

	@Override
	public String getName() {
		return this.fileName;
	}

	@Override
	public String getOriginalFilename() {
		return this.originalFilename;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public boolean isEmpty() {
		return this.empty;
	}

	@Override
	public long getSize() {
		return this.size;
	}

	@Override
	public byte[] getBytes() {
		return this.bytes;
	}

	@Override
	public InputStream getInputStream() {
		return this.inputStream;
	}

	@Override
	public void transferTo(File dest) throws IllegalStateException {
	}
}
