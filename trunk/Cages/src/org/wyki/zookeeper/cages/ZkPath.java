package org.wyki.zookeeper.cages;

import java.util.Iterator;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.KeeperException.Code;

import com.google.common.base.Splitter;

public class ZkPath extends ZkSyncPrimitive {

	final String path;
	final Iterable<String> pathNodes;
	final CreateMode createMode;
	Iterator<String> node;
	String createPath;
	
	public ZkPath(String path, CreateMode createMode) {
		super(ZkSessionManager.instance());
		this.path = path;
		this.pathNodes = Splitter.on("/").omitEmptyStrings().split(path);
		this.createMode = createMode;
		pathCreator.run();
	}
	
	public String getPath() {
		return path;
	}
	
	private Runnable pathCreator = new Runnable() {

		@Override
		public void run() {
			if (node == null) {
				node = pathNodes.iterator();
				createPath = "";
			}
			if (!node.hasNext()) {
				onStateUpdated();
			} else {
				createPath = createPath + "/" + node.next();
				zooKeeper().create(createPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
						createMode, pathCreatorResultHandler, this);
			}
		}
		
	};
	
	private StringCallback pathCreatorResultHandler = new StringCallback() {

		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
			if (progressOrRepeat(rc, new Code[] { Code.OK, Code.NODEEXISTS}, (Runnable)ctx)) {
				pathCreator.run();
			}
		}
		
	};
}
