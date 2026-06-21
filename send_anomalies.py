import json
import time
from kafka import KafkaProducer
from kafka.errors import KafkaError

print("=" * 60)
print("🔌 ПРОВЕРКА ПОДКЛЮЧЕНИЯ К KAFKA")
print("=" * 60)

ADDRESSES = [
    'localhost:9092',
    '127.0.0.1:9092',
    'kafka_producer:9092',
    'kafka:9092'
]

producer = None
selected_address = None

for addr in ADDRESSES:
    try:
        print(f"🔌 Пробую подключиться к {addr}...")
        test_producer = KafkaProducer(
            bootstrap_servers=addr,
            value_serializer=lambda v: json.dumps(v).encode('utf-8'),
            max_block_ms=3000
        )
        test_producer.partitions_for('user-transactions')
        producer = test_producer
        selected_address = addr
        print(f"✅ ПОДКЛЮЧЕНИЕ УСПЕШНО! Использую: {addr}")
        break
    except Exception as e:
        print(f"❌ {addr} - ошибка: {str(e)[:50]}...")
        continue

if producer is None:
    print("\n" + "=" * 60)
    print("❌ НЕ УДАЛОСЬ ПОДКЛЮЧИТЬСЯ К KAFKA!")
    print("=" * 60)
    exit(1)

print("\n" + "=" * 60)
print("🚀 НАЧИНАЮ ОТПРАВКУ ВСЕХ 5 АНОМАЛИЙ")
print("=" * 60)

def send(user_id, tx_type, amount, event_time=None):
    """Отправка одной транзакции с возможностью указать время"""
    if event_time is None:
        event_time = int(time.time() * 1000)
    
    tx = {
        "user_id": user_id,
        "firstname": "Test",
        "lastname": "User",
        "type": tx_type,
        "sum": amount,
        "event_time": event_time
    }
    try:
        producer.send('user-transactions', value=tx)
        producer.flush()
        print(f"✓ user={user_id}, type={tx_type}, sum={amount}, time={event_time}")
    except Exception as e:
        print(f"❌ Ошибка отправки: {e}")
        raise
    time.sleep(0.1)

# ============================================
# 1. NEGATIVE_M (user=1)
# ============================================
print("\n📌 1. NEGATIVE_M (user=1)")
print("-" * 40)
base_time = int(time.time() * 1000)
send(1, "Deposit", 10000, base_time)
for i in range(5):
    send(1, "Credit", 15000, base_time + (i + 1) * 10000)  # каждые 10 секунд
print("✅ NEGATIVE_M отправлена")

# ============================================
# 2. BIGGER_THEN_AVG_CHECK (user=2)
# ============================================
print("\n📌 2. BIGGER_THEN_AVG_CHECK (user=2)")
print("-" * 40)
base_time = int(time.time() * 1000)

for series in range(5):
    print(f"  Серия {series + 1}/5")
    series_time = base_time + series * 60000  # каждые 1 минуту
    
    # 4 маленькие транзакции (каждые 2 секунды)
    for i in range(4):
        send(2, "Deposit", 500, series_time + i * 2000)
    
    # 1 крупная транзакция
    send(2, "Credit", 2000, series_time + 8000)
    time.sleep(0.5)

print("✅ BIGGER_THEN_AVG_CHECK отправлена")

# ============================================
# 3. BIGGEST_AND_FREQUENT_CREDIT (user=135) - ИСПРАВЛЕНО!
# ============================================
print("\n📌 3. BIGGEST_AND_FREQUENT_CREDIT (user=135)")
print("-" * 40)
print("   Настройки: LARGE_CREDIT_THRESHOLD=10000, MIN_COUNT=5")
print("   Отправляем 5 кредитов по 15000 руб за 30 минут")

base_time = int(time.time() * 1000)

# Отправляем 5 кредитов с интервалом 5 минут (все в окне 30 минут)
for i in range(5):
    event_time = base_time + i * 300000  # +5 минут каждый раз
    send(135, "Credit", 15000, event_time)
    print(f"    Кредит #{i+1}: через {i*5} минут")
    time.sleep(0.3)

print("✅ BIGGEST_AND_FREQUENT_CREDIT отправлена (5 кредитов по 15000 за 20 минут)")

# ============================================
# 4. STRUCTURING_SMALL_TRANSACTIONS (user=4)
# ============================================
print("\n📌 4. STRUCTURING_SMALL_TRANSACTIONS (user=4)")
print("-" * 40)
print("   Настройки: SMALL_TRANSACTION_THRESHOLD=1000, MIN_COUNT=15, MIN_TOTAL=5000")
print("   Отправляем 15 депозитов по 400 руб за 30 минут")

base_time = int(time.time() * 1000)

for i in range(15):
    event_time = base_time + i * 120000  # каждые 2 минуты
    send(4, "Deposit", 400, event_time)
    time.sleep(0.1)

print("✅ STRUCTURING_SMALL_TRANSACTIONS отправлена (15×400=6000 руб за 28 минут)")

# ============================================
# 5. EXCESSIVE_REVERSAL_PATTERN (user=5)
# ============================================
print("\n📌 5. EXCESSIVE_REVERSAL_PATTERN (user=5)")
print("-" * 40)
print("   Настройки: REVERSAL_THRESHOLD=0.85, MAX_TIME=5 мин, MIN_COUNT=3")
print("   Отправляем 3 паттерна 'депозит→кредит' за 1 час")

base_time = int(time.time() * 1000)

# Паттерн #1
print("  Паттерн #1: 100,000 → 90,000 (90%, 1 мин)")
send(5, "Deposit", 100000, base_time)
send(5, "Credit", 90000, base_time + 60000)
time.sleep(0.5)

# Паттерн #2
print("  Паттерн #2: 80,000 → 72,000 (90%, 1 мин)")
send(5, "Deposit", 80000, base_time + 300000)
send(5, "Credit", 72000, base_time + 360000)
time.sleep(0.5)

# Паттерн #3
print("  Паттерн #3: 120,000 → 105,000 (87.5%, 1 мин)")
send(5, "Deposit", 120000, base_time + 600000)
send(5, "Credit", 105000, base_time + 660000)

print("✅ EXCESSIVE_REVERSAL_PATTERN отправлена")

# ============================================
# ИТОГ
# ============================================
print("\n" + "=" * 60)
print("✅ ВСЕ 5 АНОМАЛИЙ УСПЕШНО ОТПРАВЛЕНЫ!")
print("=" * 60)
print("\n📊 ОЖИДАЕМЫЕ АНОМАЛИИ:")
print("  1. NEGATIVE_M - user=1 (после 5-го Credit)")
print("  2. BIGGER_THEN_AVG_CHECK - user=2 (после 5-й серии)")
print("  3. BIGGEST_AND_FREQUENT_CREDIT - user=3 (после 5-го Credit)")
print("  4. STRUCTURING_SMALL_TRANSACTIONS - user=4 (после 15-й транзакции)")
print("  5. EXCESSIVE_REVERSAL_PATTERN - user=5 (после 3-го паттерна)")
print("\n📋 ПРОВЕРЬТЕ РЕЗУЛЬТАТЫ:")
print("  docker logs spark-transaction-consumer --tail 100 | grep -E '(user=3|BIGGEST|largeCreditCount)'")
print("  docker exec kafka_producer bash -c 'kafka-console-consumer --bootstrap-server localhost:9092 --topic alerts --from-beginning --max-messages 10'")

producer.close()