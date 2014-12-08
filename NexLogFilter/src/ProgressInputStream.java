import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ProgressInputStream extends FilterInputStream {

	private final double maxbytes;
    private long current = 0;
    
    /* Progress bar���� ���� ������� ���� �� �� �ִ� ���� ���� ������ ���� Ŭ����
     * ������ �ѱ��̸� ��ü���� �� �� �޾ƿ���, ���� ��ŭ �о����� ���� �� ���� byte������ counting�Ѵ�. */
	protected ProgressInputStream(InputStream paramInputStream, long bytesexpect) {
		super(paramInputStream);
		// TODO Auto-generated constructor stub
		maxbytes = (double)bytesexpect;
	}
	
	/* ������� ���� Byte / ���� �� ����(Byte) */
	public double getProgress(){
		return current / maxbytes;
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		final int ret = super.read();
		if (ret >= 0) {
			current++;
		}
		return ret;
	}

	@Override
	public int read(byte[] paramArrayOfByte) throws IOException {
		// TODO Auto-generated method stub
		final int ret = super.read(paramArrayOfByte);
		current += ret;
		return ret;
	}

	@Override
	public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
			throws IOException {
		// TODO Auto-generated method stub
		final int ret = super.read(paramArrayOfByte, paramInt1, paramInt2);
		current += ret;
		return ret;
	}

	@Override
	public long skip(long paramLong) throws IOException {
		// TODO Auto-generated method stub
		final long ret = super.skip(paramLong);
		current += ret;
		return ret;
	}
}
