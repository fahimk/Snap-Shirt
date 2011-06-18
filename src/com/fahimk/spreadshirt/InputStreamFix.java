package com.fahimk.spreadshirt;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

//problem with the regular input stream returning null objects
//code from by soule.thomas posted on
//http://code.google.com/p/android/issues/detail?id=6066

public class InputStreamFix extends FilterInputStream {
	  
	public InputStreamFix(InputStream in) {
	    super(in);
	  }
	  
	  public long skip(long n) throws IOException {
		  long m = 0L;
		  while (m < n) {
			  long _m = in.skip(n-m);
			  if (_m == 0L) break;
			  m += _m;
		  }
		  return m;
	  }
}