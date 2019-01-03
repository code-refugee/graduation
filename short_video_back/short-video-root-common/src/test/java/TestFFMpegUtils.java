import com.yuhangTao.utils.FFMpegUtils;
import org.junit.Before;
import org.junit.Test;

public class TestFFMpegUtils {

    private FFMpegUtils ffMpegUtils;

    @Before
    public void doBefore(){
        ffMpegUtils=new FFMpegUtils("D:/工具/FFmpeg/ffmpeg/bin/ffmpeg.exe");
    }

    @Test
    public void testCutMusic(){
//        ffMpegUtils.cutMusic("F:/graduation/lab/music/Che'Nelle - SAKURA.flac",
//                "00:00:00","00:00:25",
//                "F:/graduation/music/Che'Nelle - SAKURA.flac");
    }
}
