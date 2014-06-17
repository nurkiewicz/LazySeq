package com.nurkiewicz.lazyseq.samples;

class Record {
	private final int id;

	Record(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Record)) return false;
		Record record = (Record) o;
		return id == record.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}