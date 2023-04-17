-- 加锁脚本 version 1.0 仿写Redisson tryLockInnerAsync方法中lua脚本
-- keys1：要加锁的名称 argv1：锁存活的时间ms argv2:当前线程或主机的地址
local expire_time = tonumber(ARGV[1])

-- 锁不存在
if redis.call('exists', KEYS[1]) == 0 then
    -- 创建锁，用hash类型存储，value为当前执行占锁线程，防止线程间误删锁
    redis.call('hset', KEYS[1], ARGV[2], 1)
    -- 设置锁的存活时间，防止死锁
    redis.call('pexpire', KEYS[1], expire_time)
    return 1
end

-- 锁存在
if redis.call('hexists', KEYS[1], ARGV[2]) == 1 then
    -- 表示是同一线程重入
    redis.call('hincrby', KEYS[1], ARGV[2], 1)
    -- 续期
    redis.call('pexpire', KEYS[1], expire_time)
    return 1
end

-- 没抢到锁，返回锁的剩余有效时间ms
return redis.call('pttl', KEYS[1])



