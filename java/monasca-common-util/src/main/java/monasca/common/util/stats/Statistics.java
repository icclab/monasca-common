/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.util.stats;

/**
 * Statistic implementations.
 */
public final class Statistics {
  public static abstract class AbstractStatistic<T> implements Statistic<T> {
    protected boolean initialized;
    protected T value;

    @Override
    public boolean isInitialized() {
      return initialized;
    }

    @Override
    public void reset() {
      initialized = false;
      value = null;
    }

    @Override
    public String toString() {
      return String.valueOf(value());
    }

    @Override
    public T value() {
      return !initialized ? null : value;
    }
  }

  public static class Average extends Sum {
    protected int count;
    
    public Average() {
    	reset();
    }
    
    @Override
    public void addValue(Double value) {
      super.addValue(value);
      this.count++;
    }
    
    @Override
		public void addValue(String value) {
			try {
				addValue(Double.parseDouble(value));
			} catch (Exception e) {}
		}

    @Override
    public Double value() {
      return !initialized ? Double.NaN : count == 0 ? 0 : value / count;
    }
    
    @Override
    public void reset() {
    	initialized = false;
      value = 0.0;
      count = 0;
    }
  }

  public static class Count extends AbstractStatistic<Double> {
  	public Count() {
  		reset();
  	}
  	
    @Override
    public void addValue(Double value) {
    	initialized = true;
      this.value++;
    }

		@Override
		public void addValue(String value) {
			try {
				addValue(Double.parseDouble(value));
			} catch (Exception e) {}
		}
		
		@Override
    public void reset() {
      initialized = false;
      value = 0.0;
    }
		
		@Override
    public Double value() {
      return !initialized ? Double.NaN : value;
    }
  }

  public static class Max extends AbstractStatistic<Double> {
   @Override
    public void addValue(Double value) {
      if (!initialized) {
        initialized = true;
        this.value = value;
      } else if (value > this.value)
        this.value = value;
    }

		@Override
		public void addValue(String value) {
			try {
				addValue(Double.parseDouble(value));
			} catch (Exception e) {}
		}
		
		@Override
    public Double value() {
      return !initialized ? Double.NaN : value;
    }
  }

  public static class Min extends AbstractStatistic<Double> {
  	@Override
		public void addValue(Double value) {
  		if (!initialized) {
        initialized = true;
        this.value = value;
      } else if (value < this.value)
        this.value = value;
		}

		@Override
		public void addValue(String value) {
			try {
				addValue(Double.parseDouble(value));
			} catch (Exception e) {}
		}
		
		@Override
    public Double value() {
      return !initialized ? Double.NaN : value;
    }
  }

  public static class Sum extends AbstractStatistic<Double> {
  	public Sum() {
  		reset();
  	}
  	
		@Override
		public void addValue(Double value) {
			initialized = true;
			this.value += value;
		}

		@Override
		public void addValue(String value) {
			try {
				addValue(Double.parseDouble(value));
			} catch (Exception e) {}
		}
		
		@Override
    public void reset() {
      initialized = false;
      value = 0.0;
    }
		
		@Override
    public Double value() {
      return !initialized ? Double.NaN : value;
    }
  }
  
  public static class Concat extends AbstractStatistic<String> {
  	@Override
		public void addValue(String value) {
			if (!initialized) {
        initialized = true;
        this.value = value;
      } else
        this.value = this.value.concat(value);
		}
  }

  private Statistics() {
  }
}
