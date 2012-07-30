package info.dourok.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
public class AppCacheCookieStore extends InMemoryCookieStore {
	private static final String FILE_NAME = "info.dourok.net.AppCacheCookieStore";
	private Context mContext;
	private File mFile;
	private ReentrantLock lock = null;
	private static AppCacheCookieStore singleton;

	public static AppCacheCookieStore getInstance(Context context)
			throws IOException {
		if (singleton == null) {
			singleton = new AppCacheCookieStore(context);
		}
		return singleton;

	}

	public static AppCacheCookieStore getInstance() {
		if (singleton == null)
			throw new IllegalMonitorStateException("Context are not set");
		return singleton; 
	}

	private AppCacheCookieStore(Context context) throws IOException {
		if (context == null) {
			throw new IllegalArgumentException("context can't not be null");
		}
		mContext = context;

		mFile = new File(mContext.getCacheDir().getAbsolutePath() + "/"
				+ FILE_NAME);
		if (!mFile.exists()) {
			mFile.createNewFile();
		}
		lock = new ReentrantLock(false);
		load();
	}

	public void store() throws FileNotFoundException, IOException {
		ObjectOutputStream oos = null;
		Log.d(getClass().getSimpleName(), "store");
		try {
			oos = new ObjectOutputStream(new FileOutputStream(mFile, false));
			writeList(cookieJar, oos);
			writeDomainIndex(domainIndex, oos);
			writeUriIndex(uriIndex, oos);
		} finally {
			if (oos != null)
				oos.close();
			load();
		}
	}
	
	public void load() throws StreamCorruptedException, FileNotFoundException,
			IOException {
		Log.d(getClass().getSimpleName(), "load");
		ObjectInputStream ois = null;
		try {

			try {
				ois = new ObjectInputStream(new FileInputStream(mFile));
				cookieJar = readList(ois);
				domainIndex = readDomainIndex(ois);
				uriIndex = readUriIndex(ois);
			} catch (Exception ex) {
				ex.printStackTrace();
				cookieJar = new ArrayList<HttpCookie>();
				domainIndex = new HashMap<String, List<HttpCookie>>();
				uriIndex = new HashMap<URI, List<HttpCookie>>();
			}
			if (cookieJar == null || domainIndex == null || uriIndex == null) {
				cookieJar = new ArrayList<HttpCookie>();
				domainIndex = new HashMap<String, List<HttpCookie>>();
				uriIndex = new HashMap<URI, List<HttpCookie>>();
			}
		} finally {
			if (ois != null)
				ois.close();
		}
	}

	private void writeUriIndex(Map<URI, List<HttpCookie>> map,
			ObjectOutputStream oos) throws IOException {
		int size = map.size();
		oos.writeInt(size);
		Set<URI> keys = map.keySet();
		for (URI k : keys) {
			oos.writeObject(k);
			Log.d(getClass().getSimpleName(), "store:" + k);
			writeList(map.get(k), oos);
		}
	}

	private Map<URI, List<HttpCookie>> readUriIndex(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		int size = ois.readInt();
		Map<URI, List<HttpCookie>> map = new HashMap<URI, List<HttpCookie>>(
				size + 7);
		for (int i = 0; i < size; i++) {
			URI k = (URI) ois.readObject();
			Log.d(getClass().getSimpleName(), "readUriIndex:" + k );
			List<HttpCookie> list = readList(ois);
			map.put(k, list);
		}
		return map;
	}

	private void writeDomainIndex(Map<String, List<HttpCookie>> map,
			ObjectOutputStream oos) throws IOException {
		int size = map.size();
		oos.writeInt(size);
		Set<String> keys = map.keySet();
		for (String k : keys) {
			oos.writeObject(k);
			Log.d(getClass().getSimpleName(), "store:" + k);
			writeList(map.get(k), oos);
		}
	}

	private Map<String, List<HttpCookie>> readDomainIndex(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		int size = ois.readInt();
		Map<String, List<HttpCookie>> map = new HashMap<String, List<HttpCookie>>(
				size + 7);
		for (int i = 0; i < size; i++) {

			String k = (String) ois.readObject();
			Log.d(getClass().getSimpleName(), "readDomainIndex:" + k );
			List<HttpCookie> list = readList(ois);
			map.put(k, list);
		}
		return map;
	}

	private void writeList(List<HttpCookie> list, ObjectOutputStream oos)
			throws IOException {
		int size = list.size();
		oos.writeInt(size);
		for (HttpCookie cookie : list) {
			Log.d(getClass().getSimpleName(), "store:" + cookie);
			oos.writeObject(cookie);
		}
	}

	private List<HttpCookie> readList(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		int size = ois.readInt();
		ArrayList<HttpCookie> list = new ArrayList<HttpCookie>(size + 7);

		for (int i = 0; i < size; i++) {
			HttpCookie cookie = (HttpCookie) ois.readObject();
			Log.d(getClass().getSimpleName(), "readList:" + cookie);
			list.add(cookie);
		}
		return list;
	}

	
	
	@Override
	public void add(URI uri, HttpCookie cookie) {
		// TODO Auto-generated method stub
		super.add(uri, cookie);
		try {
			lock.lock();
			store();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}

	@Override
	public boolean remove(URI uri, HttpCookie ck) {
		// TODO Auto-generated method stub
		boolean b = super.remove(uri, ck);
		try {
			lock.lock();
			store();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		return b;
	}

	@Override 
	public boolean removeAll() {
		// TODO Auto-generated method stub
		boolean b = super.removeAll();
		try {
			lock.lock();
			store();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		return b;
	}

}
