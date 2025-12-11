package io.github.luversof.boot.connectioninfo;

import java.util.Objects;

/**
 * connection info가 최종적으로 생성하려고 하는 대상 객체
 * @param <T>
 */
public class ConnectionInfo<T> {
	
	/**
	 * connection info key
	 */
	private final ConnectionInfoKey key;
	
	/**
	 * connection 객체
	 */
	private final T connection;

	public ConnectionInfo(ConnectionInfoKey key, T connection) {
		this.key = key;
		this.connection = connection;
	}

	public ConnectionInfoKey getKey() {
		return key;
	}

	public T getConnection() {
		return connection;
	}

	@Override
	public String toString() {
		return "ConnectionInfo(key=" + this.getKey() + ", connection=" + this.getConnection() + ")";
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof ConnectionInfo)) return false;
		final ConnectionInfo<?> other = (ConnectionInfo<?>) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$key = this.getKey();
		final Object other$key = other.getKey();
		if (!Objects.equals(this$key, other$key)) return false;
		final Object this$connection = this.getConnection();
		final Object other$connection = other.getConnection();
		if (!Objects.equals(this$connection, other$connection)) return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof ConnectionInfo;
	}

	@Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $key = this.getKey();
		result = result * PRIME + ($key == null ? 43 : $key.hashCode());
		final Object $connection = this.getConnection();
		result = result * PRIME + ($connection == null ? 43 : $connection.hashCode());
		return result;
	}
	
}
