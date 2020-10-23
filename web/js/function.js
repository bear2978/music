// 显示搜索界面
$("#searchBtn").click(function () {
    document.getElementById("searchWindow").hidden = false;
});
// 关闭搜索界面
$("#closeWindow").click(function () {
    document.getElementById("searchWindow").hidden = true;
});
// 清空列表
$("#clearList").click(function () {
    let player = Player.instance;
    player.musics.songs = [];
    // 更新列表
    player.renderSongList();
});
// 搜索
function doSearch() {
    // 解绑添加按钮的点击事件 否则会多次触发
    $(".addBtn").unbind("click");
    // 向后端请求搜索的数据
    $.ajax({
        url: "getMusic",
        type: "post",
        dataType: "json",
        data: {
            "keyword": $("#searchInput").val(),
            "dataSource": $("input[type='radio']:checked").val()
        },
        success: function (data) {
            localStorage.setItem("musicInfo", JSON.stringify(data));
            // 将返回的数据展示在table中
            let head = "<tr><th>歌曲名</th><th>歌手名</th><th>操作</th></tr>";
            let td = "";
            let list = eval(data);
            $.each(list, function (i, item) {
                td += "<tr><td>" + item.songName + "</td><td>" + item.singerName + "</td><td><button class='addBtn' id='" + i + "'>添加</button></td></tr>"
            });
            $("#result").html(head + td);
            // 给每个添加按钮添加点击事件
            $(".addBtn").click(function (e) {
                e.stopPropagation();
                e.preventDefault();
                // 返回的数据存到localstorage中
                let temp = JSON.parse(localStorage.getItem("musicInfo"))[this.id];
                let player = Player.instance;
                let id = player.musics.songs.length;
                let title = temp.singerName + " - " + temp.songName;
                let singer = temp.singerName;
                let songUrl = temp.songUrl;
                let song = {
                    id: id,
                    title: title,
                    singer: singer,
                    songUrl: songUrl,
                    imageUrl: './images/pic.jpg'
                };
                // 歌单列表添加对应的歌曲
                player.musics.songs.push(song);
                alert("添加成功");
                // 更新列表
                player.renderSongList();
            });
        }
    });
}