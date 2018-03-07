package com.sonic.website.redis.jedis.proxy;

import java.io.Closeable;

import redis.clients.jedis.AdvancedBinaryJedisCommands;
import redis.clients.jedis.AdvancedJedisCommands;
import redis.clients.jedis.BasicCommands;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.BinaryScriptingCommands;
import redis.clients.jedis.ClusterCommands;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.ScriptingCommands;
import redis.clients.jedis.SentinelCommands;

public interface JedisHandle extends JedisCommands, MultiKeyCommands, AdvancedJedisCommands, ScriptingCommands,
            BasicCommands, ClusterCommands, SentinelCommands, BinaryJedisCommands, MultiKeyBinaryCommands,
            AdvancedBinaryJedisCommands, BinaryScriptingCommands, Closeable {

}
