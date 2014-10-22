package cn.org.octopus.circle;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends Activity {

	private static CircleProcess circle_process;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //加载 Fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        new CircleProcessAnimation().execute();
    }

    /**
     * 设置 异步任务, 在这个任务中 设置 圆形进度条的进度值
     * @author octopus 
     *
     */
    class CircleProcessAnimation extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			for(int i = 1; i <= 360; i ++){
				try {
					//激活圆形进度条显示方法
					publishProgress(i);
					//每隔 50 毫秒更新一次数据
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			//为圆形进度条组件设置进度值
			circle_process.setmProcessValue(values[0]);
			//刷新圆形进度条显示
			circle_process.invalidate();
		}
    	
    }
    
    /**
     * 界面显示的 Fragment 
     * @author octopus
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            circle_process = (CircleProcess) rootView.findViewById(R.id.circle_process);
            return rootView;
        }
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
