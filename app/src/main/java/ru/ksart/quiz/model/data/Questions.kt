package ru.ksart.quiz.model.data

object Questions {
    fun getQuestions(): List<Question> {
        return listOf(
            Question(
                question = "Коллекции хранят:",
                answers = listOf(
                    Answer(
                        text = "Примитивные типы",
                        value = 0
                    ),
                    Answer(
                        text = "Объекты указанного типа",
                        value = 1
                    ),
                    Answer(
                        text = "Данные типа String",
                        value = 0
                    ),
                    Answer(
                        text = "Данные типа Collection",
                        value = 0
                    ),
                    Answer(
                        text = "Nothihg?",
                        value = 0
                    ),
                ),
                answerSelected = -1
            ),
            Question(
                question = "Чего не предоставляет data class в Kotlin?",
                answers = listOf(
                    Answer(
                        text = "Авто-генерируемый метод toString()",
                        value = 0
                    ),
                    Answer(
                        text = "Метод copy(...), для создания копии экземпляров.",
                        value = 0
                    ),
                    Answer(
                        text = "Автоматическое преобразование из/в JSON",
                        value = 1
                    ),
                    Answer(
                        text = "Авто-генерируемые методы hashCode() и equals()",
                        value = 0
                    ),
                    Answer(
                        text = "Метод componentN()",
                        value = 0
                    ),
                ),
                answerSelected = -1
            ),
            Question(
                question = "Какое утверждение ложно для Companion object в Kotlin?",
                answers = listOf(
                    Answer(
                        text = "Companion object имеет доступ ко всем членам класса",
                        value = 1
                    ),
                    Answer(
                        text = "У класса может быть только один Companion Object",
                        value = 0
                    ),
                    Answer(
                        text = "По-умолчанию имя Companion object (например, если мы обращаемся к нему из Java) - \"Companion\". можно задать другое имя",
                        value = 0
                    ),
                    Answer(
                        text = "Для Companion object можно создать функции-расширения или свойства-расширения",
                        value = 0
                    ),
                    Answer(
                        text = "Все утверждения верны!",
                        value = 0
                    ),
                ),
                answerSelected = -1
            ),
            Question(
                question = "Дано утверждение \"Класс или интерфейс может быть ковариантным по одному параметру типа и контрвариантным по другому.\". Для обобщенных типов в Kotlin:",
                answers = listOf(
                    Answer(
                        text = "Верно только в том случае, когда все параметры имеют одинаковый тип",
                        value = 0
                    ),
                    Answer(
                        text = "Зависит от сигнатур методов класса или интерфейса",
                        value = 0
                    ),
                    Answer(
                        text = "В Kotlin нет обобщенных типов",
                        value = 0
                    ),
                    Answer(
                        text = "Утверждение верно",
                        value = 1
                    ),
                    Answer(
                        text = "Утверждение ложно",
                        value = 0
                    ),
                ),
                answerSelected = -1
            ),
            Question(
                question = "Основные компоненты Android application?",
                answers = listOf(
                    Answer(
                        text = "Activity, Service, Content Provider, Broadcast receiver и, начиная с Android 3.0 (API 11), Fragment",
                        value = 0
                    ),
                    Answer(
                        text = "Activity, Service, Content Provider и Broadcast receiver",
                        value = 1
                    ),
                    Answer(
                        text = "Activity, Service, Content Provider, Broadcast receiver, Fragment (начиная с Android 3.0 (API 11)) и Intent",
                        value = 0
                    ),
                    Answer(
                        text = "Activity, Service, Content Provider, Broadcast receiver, Fragment (начиная с Android 3.0 (API 11)), Intent и AndroidManifest",
                        value = 0
                    ),
                    Answer(
                        text = "Ничего из перечисленного",
                        value = 0
                    ),
                ),
                answerSelected = -1
            ),
        )
    }
}