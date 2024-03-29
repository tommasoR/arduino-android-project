package ioio.examples.simple;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class IOIOSimpleApp extends AbstractIOIOActivity {
	private TextView textView_;
	private SeekBar seekBar_;
	private ToggleButton toggleButton_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView_ = (TextView)findViewById(R.id.TextView);
        seekBar_ = (SeekBar)findViewById(R.id.SeekBar);
        toggleButton_ = (ToggleButton)findViewById(R.id.ToggleButton);

        enableUi(false);
    }
	
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private AnalogInput input_;
		private PwmOutput pwmOutput_;
		private DigitalOutput led_;

		
		public void setup() throws ConnectionLostException {
			try {
				input_ = ioio_.openAnalogInput(40);
				pwmOutput_ = ioio_.openPwmOutput(12, 100);
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
				enableUi(true);
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}
		
		public void loop() throws ConnectionLostException {
			try {
				final float reading = input_.read();
				setText(Float.toString(reading));
				pwmOutput_.setPulseWidth(1000 + seekBar_.getProgress());
				led_.write(!toggleButton_.isChecked());
				sleep(10);
			} catch (InterruptedException e) {
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				seekBar_.setEnabled(enable);
				toggleButton_.setEnabled(enable);
			}
		});
	}
	
	private void setText(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView_.setText(str);
			}
		});
	}
}