package com.devux.finflow.utils

object NotificationContent {

    // 1. BUỔI TỐI (20:00): Ghi chép, Check var, Sao kê
    private val eveningMessages = listOf(
        "Check VAR cực căng! ⚽️" to "Trọng tài yêu cầu bạn check lại các khoản chi hôm nay. Có việt vị không?",
        "Đúng nhận sai cãi giúp cái! 🍎" to "Hôm nay tiêu gì thì khai mau, đừng để cái ví nó dỗi!",
        "Đừng Overthinking nữa! 🤯" to "Tiền đi đâu hết rồi? Ghi chép lại ngay để tâm hồn được chữa lành.",
        "Flex nhẹ cái ví tiền nào 💪" to "Người thành công là người ghi chép đầy đủ. Vào app thể hiện đẳng cấp đi!",
        "Hôm nay có mua gì 'vô tri' không? 🗿" to "Khai thật đi, mình hứa không mách mẹ đâu.",
        "Alo, Cảnh sát tài chính đây! 👮‍♂️" to "Yêu cầu bạn khai báo thành khẩn các khoản chi hôm nay!",
        "Ting ting! 🤑 Chốt sổ thôi!" to "Dành 1 phút tổng kết để tối nay ngủ ngon không mộng mị.",
        "Kiếp nạn thứ 82: Hết tiền 🌪️" to "Ghi chép ngay để bảo toàn tính mạng cho cái ví!",
        "Sao kê đi bạn ơi! 📄" to "Minh bạch tài chính là sức mạnh. Vào app sao kê ngay!"
    )

    // 2. BUỔI SÁNG (08:00): Thử thách, Tín hiệu vũ trụ, Động lực
    private val morningMessages = listOf(
        "Tín hiệu vũ trụ 🌌" to "Vũ trụ gửi thông điệp: Hôm nay đừng uống trà sữa, hãy uống nước lọc!",
        "Chữa lành ví tiền ❤️‍🩹" to "Thử thách 24h không tiêu hoang để chữa lành nỗi đau viêm màng túi.",
        "Mang tiền về cho mẹ 🎶" to "Đừng mang ưu phiền về cho mẹ. Hôm nay tiết kiệm 50k nhé!",
        "Sống tỉnh thức 🧘" to "Hôm nay mua gì nhớ niệm thần chú: 'Mình có thực sự cần nó không?'",
        "Kèo máu: 24h không tiêu tiền! ⚔️" to "Nếu thua: Chống đẩy 20 cái hoặc nhịn ăn vặt ngày mai.",
        "Cảnh báo: Ví đang mỏng! 📉" to "Hôm nay hãy bật chế độ 'Nhà nghèo vượt khó' nhé!",
        "Thử thách: Cai nghiện Shopee 📦" to "Hôm nay cấm mở app mua sắm. Làm được không?",
        "Giữ chặt ví tiền! b" to "Đừng để tiền rơi. Hôm nay cố gắng không tiêu quá 100k nhé!"
    )

    fun getEveningMessage() = eveningMessages.random()
    fun getMorningMessage() = morningMessages.random()
}