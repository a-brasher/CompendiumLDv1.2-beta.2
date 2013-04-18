/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.learningdesign.util;



public enum TimeUnit {
NANOSECONDS(0), MICROSECONDS(1), MILLISECONDS(2), SECONDS(3), MINUTES(4), HOURS(5), DAYS(6);

/** the index of this unit */
private final int index;

/** Internal constructor */
TimeUnit(int index) { 
    this.index = index; 
}

/** Lookup table for conversion factors */
private static final int[] multipliers = { 
    1, 
    1000, 
    1000 * 1000, 
    1000 * 1000 * 1000,
    1000 * 1000* 1000 * 60,
    1000 * 1000* 1000 * 60 * 60,
    1000 * 1000* 1000 * 60 * 60 * 24
};

/** Lookup table for conversion factors between adjacent units */
private static final int[] factors = {  	
    1000,  						//nano to micro 	0
    1000, 						//micro to milli	1
    1000,						//milli to seconds	2
    60,							//seconds to minutes	3
    60,							//minutes to hours	4
    24							//hours to days		5
};


	  /** 
     * Lookup table to check saturation.  Note that because we are
     * dividing these down, we don't have to deal with asymmetry of
     * MIN/MAX values.
     */
    private static final long[] overflows = { 
        0, // unused
        Long.MAX_VALUE / 1000,
        Long.MAX_VALUE / (1000 * 1000),
        Long.MAX_VALUE / (1000 * 1000 * 1000),
        Long.MAX_VALUE / (1000 * 1000 * 1000* 60),
        Long.MAX_VALUE / (1000 * 1000 * 1000* 60 * 60),
        Long.MAX_VALUE / (1000 * 1000 * 1000* 60 * 60*24)
    };

    /**
     * Perform conversion based on given delta representing the
     * difference between units
     * @param delta the difference in index values of source and target units
     * @param duration the duration
     * @return converted duration or saturated value
     */
    private static long doConvert(int delta, long duration) {
        if (delta == 0)
            return duration;
        if (delta < 0) 
            return duration / multipliers[-delta];
        if (duration > overflows[delta])
            return Long.MAX_VALUE;
        if (duration < -overflows[delta])
            return Long.MIN_VALUE;
        return duration * multipliers[delta];
    }
    
    /**
     * Perform conversion based on given delta representing the
     * difference between units
     * @param delta the difference in index values of source and target units
     * @param duration the duration
     * @return converted duration or saturated value
     */
    private static float doConvert(int nFirstUnit, int nSecondUnit, long duration) {
    	long initDuration = duration;
    	float fNewDuration = duration;
    /**	 if (duration > overflows[delta])
             return Long.MAX_VALUE;
         if (duration < -overflows[delta])	
         	   return Long.MIN_VALUE;	**/
        if (nFirstUnit == nSecondUnit)
            return duration;
        if (nFirstUnit < nSecondUnit)	{
        	for (int i=nFirstUnit; i<nSecondUnit; ++i)	{
        		fNewDuration = fNewDuration/factors[i];
        	}
        return fNewDuration;	
        }
        else if (nSecondUnit < nFirstUnit)	{
        	for (int i=nSecondUnit; i<nFirstUnit; ++i)	{
        		fNewDuration = fNewDuration/factors[i];
        	}
        return fNewDuration;	
        }
      return fNewDuration;
    }

    /**
     * Convert the given time duration in the given unit to this
     * unit.  Conversions from finer to coarser granularities
     * truncate, so lose precision. For example converting
     * <tt>999</tt> milliseconds to seconds results in
     * <tt>0</tt>. Conversions from coarser to finer granularities
     * with arguments that would numerically overflow saturate to
     * <tt>Long.MIN_VALUE</tt> if negative or <tt>Long.MAX_VALUE</tt>
     * if positive.
     *
     * @param duration the time duration in the given <tt>unit</tt>
     * @param unit the unit of the <tt>duration</tt> argument
     * @return the converted duration in this unit,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     */
    public long convert(long duration, TimeUnit unit) {
        return doConvert(unit.index - index, duration);
    }

    /**
     * Equivalent to <tt>NANOSECONDS.convert(duration, this)</tt>.
     * @param duration the duration
     * @return the converted duration,
     * or <tt>Long.MIN_VALUE</tt> if conversion would negatively
     * overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
     * @see #convert
     */
    public long toNanos(long duration) {
        return doConvert(index, duration);
    }

    public float toMinutes(long duration)	{

		return doConvert(index, MINUTES.index,  duration);

	}

	public float toHours(long duration)	{

		return doConvert(index, HOURS.index,  duration);

	}
	
	public float toDays(long duration)	{

		return doConvert(index, DAYS.index,  duration);

	}

}
