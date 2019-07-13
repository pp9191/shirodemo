// Global defaults
jconfirm.defaults = {
    title: '提示',
    backgroundDismissAnimation: '',
    defaultButtons: {
        ok: {
        	text: '确定',
            action: function () {
            }
        },
        close: {
        	text: '取消',
            action: function () {
            }
        },
    },
};


$.extend($.fn.datepicker.defaults, {
	language: 'zh-CN',
    weekStart: 1,
    todayBtn:  "linked",   //今日日期按钮
	autoclose: true,   //自动关闭
	todayHighlight: true,   //高亮今日日期
	startDate: '1900-01-01',
	endDate: "today"
});

function getCurrentDialog(){
	var length = jconfirm.instances.length;
	if(length > 0){		
		return jconfirm.instances[length - 1]
	}else{
		return null;
	}
}