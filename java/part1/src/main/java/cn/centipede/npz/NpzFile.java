package cn.centipede.npz;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.centipede.numpy.NDArray;

/**
 * A ZIP file where each individual file is in NPY format.
 *
 * By convention each file has `.npy` extension, however, the API doesn't expose
 * it. So for instance the array named "X" will be accessibly via "X" and
 * **not** "X.npy".
 *
 * Example:
 *
 * @sample [org.jetbrains.bio.npy.npzExample]
 */
public class NpzFile {
    private ZipFile zf;

    public NpzFile(Path path) {
        try {
            zf = new ZipFile(path.toFile(), ZipFile.OPEN_READ, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<NpzEntry> introspect() {
        List<NpzEntry> list = zf.stream().map(this::parseHeader).collect(Collectors.toList());
        list.stream().forEach(System.out::println);
        return list;
    }

    private NpzEntry parseHeader(ZipEntry entry) {
        try {
            return parseHeader(zf, entry);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Parse Header failed!");
    }

    private ByteBuffer getBuffers(ZipEntry entry, int step) throws IOException {
        InputStream is = zf.getInputStream(entry);
        ByteBuffer chunk = ByteBuffer.allocate(0);

        int remaining = is.available();
        if (remaining > 0) {
            chunk = ByteBuffer.allocate(remaining);
        }

        Channels.newChannel(is).read(chunk);
        chunk = chunk.asReadOnlyBuffer();
        chunk.flip();
        return chunk;
    }

    private NpzEntry parseHeader(ZipFile zf, ZipEntry entry) throws IOException {
        ByteBuffer chunk = getBuffers(entry, 1<<18);
        Header header = Header.parseHeader(chunk);
        String entryName = entry.getName();
        entryName = entryName.substring(0, entryName.lastIndexOf("."));
        return new NpzEntry(entryName, header.getType(), header.shape);
    }

    public void close() {
        try {
            zf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NDArray get(String name) {
        ZipEntry entry = zf.getEntry(name + ".npy");
        try {
            return NpyFile.read(getBuffers(entry, 1 << 18));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Read Npy failed!");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = NpzFile.class.getResource("/mnist.npz");
        NpzFile npz = new NpzFile(Paths.get(url.toURI()));
        npz.get("k1").dump();
        npz.get("b1").dump();
        npz.close();
    }
}
