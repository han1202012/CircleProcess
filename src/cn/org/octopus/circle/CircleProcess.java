package cn.org.octopus.circle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleProcess extends ImageView {

	/** 画笔 */
	private Paint mPaint;
	/** 上下文对象 */
	private Context mContext;
	/** 进度条的值 */
	private int mProcessValue;
	
	public CircleProcess(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 初始化成员变量 Context
		mContext = context;
		// 创建画笔, 并设置画笔属性
		mPaint = new Paint();
		// 消除绘制时产生的锯齿
		mPaint.setAntiAlias(true);
		// 绘制空心圆形需要设置该样式
		mPaint.setStyle(Style.STROKE);
	}
	
	/**
	 * 自定义布局实现的 只有 Context 参数的构造方法
	 * @param context
	 */
	public CircleProcess(Context context) {
		super(context);
	}
	
	/**
	 * 自定义布局实现的 三个参数的构造方法
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CircleProcess(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//获取圆心的 x 轴位置
		int center = getWidth() / 2;
		/*
		 * 中间位置 x 减去左侧位置 的绝对值就是圆半径, 
		 * 注意 : 由于 padding 属性存在, |left - right| 可能与 width 不同
		 */
		int outerRadius = Math.abs(getLeft() - center);
		//计算内圆半径大小, 内圆半径 是 外圆半径的一般
		int innerRadius = outerRadius / 2;
		
		//设置画笔颜色
		mPaint.setColor(Color.BLUE);
		//设置画笔宽度
		mPaint.setStrokeWidth(2);
		//绘制内圆方法 前两个参数是 x, y 轴坐标, 第三个是内圆半径, 第四个参数是 画笔
		canvas.drawCircle(center, center, innerRadius, mPaint);
		
		/*
		 * 绘制进度条的圆弧
		 * 
		 * 绘制图形需要 left top right bottom 坐标, 下面需要计算这个坐标
		 */
		
		//计算圆弧宽度
		int width = outerRadius - innerRadius;
		//将圆弧的宽度设置给 画笔
		mPaint.setStrokeWidth(width);
		/*
		 * 计算画布绘制圆弧填入的 top left bottom right 值, 
		 * 这里注意给的值要在圆弧的一半位置, 绘制的时候参数是从中间开始绘制
		 */
		int top = center - (innerRadius + width/2);
		int left = top;
		int bottom = center + (innerRadius + width/2);
		int right = bottom;
		
		//创建圆弧对象
		RectF rectf = new RectF(left, top, right, bottom);
		//绘制圆弧 参数介绍 : 圆弧, 开始度数, 累加度数, 是否闭合圆弧, 画笔
		canvas.drawArc(rectf, 270, mProcessValue, false, mPaint);
		
		//绘制外圆
		mPaint.setStrokeWidth(2);
		canvas.drawCircle(center, center, innerRadius + width, mPaint);
		
		/*
		 * 在内部正中央绘制一个数字
		 */
		//生成百分比数字
		String str = (int)(mProcessValue * 1.0 / 360 * 100) + "%"; 
		/*
		 * 测量这个数字的宽 和 高
		 */
		//创建数字的边界对象
		Rect textRect = new Rect();
		//设置数字的大小, 注意要根据 内圆半径设置
		mPaint.setTextSize(innerRadius / 2);
		mPaint.setStrokeWidth(0);
		//获取数字边界
		mPaint.getTextBounds(str, 0, str.length(), textRect);
		int textWidth = textRect.width();
		int textHeight = textRect.height();
		
		//根据数字大小获取绘制位置, 以便数字能够在正中央绘制出来
		int textX = center - textWidth / 2;
		int textY = center + textHeight / 2;
		
		//正式开始绘制数字
		canvas.drawText(str, textX, textY, mPaint);
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		/*
		 * setMeasuredDimension 方法 : 该方法决定当前的 View 的大小
		 * 根据 View 在布局中的显示, 动态获取 View 的宽高
		 * 
		 * 当布局组件 warp_content 时 : 
		 * 从 MeasureSpec 获取的宽度 : 492 高度 836 , 
		 * 默认 宽高 都是 120dip转化完毕后 180px 
		 * 
		 * 当将布局组件 的宽高设置为 240 dp : 
		 * 宽度 和 高度 MeasureSpec 获取的都是 360, 此时 MeasureSpec 属于精准模式
		 * 
		 */
		setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
		System.out.println(getWidth() + " , " + getHeight());
		
	}
	
	/**
	 * 获取组件宽度
	 * 
	 * MeasureSpec : 该 int 类型有 32 位, 前两位是状态位, 后面 30 位是大小值;
	 * 		常用方法 : 
	 * 		-- MeasureSpec.getMode(int) : 获取模式
	 *      -- MeasureSpec.getSize(int) : 获取大小
	 *      -- MeasureSpec.makeMeasureSpec(int size, int mode) : 创建一个 MeasureSpec;
	 *      -- MeasureSpec.toString(int) : 模式 + 大小 字符串
	 *      
	 *      模式介绍 : 注意下面的数字是二进制的
	 *      -- 00 : MeasureSpec.UNSPECIFIED, 未指定模式;
	 *      -- 01 : MeasureSpec.EXACTLY, 精准模式;
	 *      -- 11 : MeasureSpec.AT_MOST, 最大模式;
	 *      
	 *      注意 : 这个 MeasureSpec 模式是在 onMeasure 方法中自动生成的, 一般不用去创建这个对象
	 *      
	 * @param widthMeasureSpec
	 * 				MeasureSpec 数值
	 * @return
	 * 				组件的宽度
	 */
	private int measure(int measureSpec) {
		//返回的结果, 即组件宽度
		int result = 0;
		//获取组件的宽度模式
		int mode = MeasureSpec.getMode(measureSpec);
		//获取组件的宽度大小 单位px
		int size = MeasureSpec.getSize(measureSpec);
		
		if(mode == MeasureSpec.EXACTLY){//精准模式
			result = size;
		}else{//未定义模式 或者 最大模式
			//注意 200 是默认大小, 在 warp_content 时使用这个值, 如果组件中定义了大小, 就不使用该值
			result = dip2px(mContext, 200);
			if(mode == MeasureSpec.AT_MOST){//最大模式
				//最大模式下获取一个稍小的值
				result = Math.min(result, size);
			}
		}
		
		return result;
	}
	
	
	/**
	 * 将手机的 设备独立像素 转为 像素值
	 * 
	 * 		公式 : px / dip = dpi / 160
	 * 			   px = dip * dpi / 160;
	 * @param context
	 * 				上下文对象
	 * @param dpValue
	 * 				设备独立像素值
	 * @return
	 * 				转化后的 像素值
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 将手机的 像素值 转为 设备独立像素
	 * 		公式 : px/dip = dpi/160
	 * 			   dip = px * 160 / dpi
	 * 			   dpi (dot per inch) : 每英寸像素数 归一化的值 120 160 240 320 480;
	 * 			   density : 每英寸的像素数, 精准的像素数, 可以用来计算准确的值
	 * 			   从 DisplayMetics 中获取的
	 * @param context
	 * 				上下文对象
	 * @param pxValue
	 * 				像素值
	 * @return
	 * 				转化后的 设备独立像素值
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取当前进度值
	 * @return
	 * 				返回当前进度值
	 */
	public int getmProcessValue() {
		return mProcessValue;
	}

	/**
	 * 为该组件设置进度值
	 * @param mProcessValue
	 * 				设置的进度值参数
	 */
	public void setmProcessValue(int mProcessValue) {
		this.mProcessValue = mProcessValue;
	}
	
}
