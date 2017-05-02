---
title: Основы
lang: ru
navigation: basics
---

Чтобы открыть **PackageTemplates** воспользуйтесь разделом **new** контекстного меню. Так же можно настроить [Сочетания клавиш][1].

![context_menu]({{ site.baseurl }}/images/tutorial/context_menu.png){: .imageFragment}


![main_dialog]({{ site.baseurl }}/images/tutorial/main_dialog.png){: .image}

1. Создать
2. Редактировать
3. Добавить\Удалить в\из **{{ site.data.const.favourites }}**.
4. Настройки Плагина
5. [Экспорт][2]
6. [Импорт][3]
7. [АвтоИмпорт][4]
8. Кнопки для быстрой вставки путей в верхнее поле ввода(для облегчения поиска)

### Выбор шаблона
Есть два варианта выбора:

**A.** Указав путь к файлу (.json)<br>
**B.** Из **{{ site.data.const.favourites }}**<br>

**Примечание:** **{{ site.data.const.favourites }}** запоминает путь к файлу. Если при запуске IDE по сохраненному пути не будет файла, то шаблон автоматически удалится из списка **{{ site.data.const.favourites }}**. 


[1]: {{ site.baseurl}}{{ site.data.links.tutorial_shortcuts[page.lang] }}
[2]: {{ site.baseurl}}{{ site.data.links.tutorial_impex[page.lang] }}#export
[3]: {{ site.baseurl}}{{ site.data.links.tutorial_impex[page.lang] }}#import
[4]: {{ site.baseurl}}{{ site.data.links.tutorial_impex[page.lang] }}#autoImport