package com.example.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.entity.MypageEntity;
import com.example.entity.TmUsrEntity;
import com.example.form.MyForm;
import com.example.mapper.LoginMapper;
import com.example.mapper.TbRsvMapper;
import com.example.service.LoginService;

import lombok.var;

/**
 * . 各画面遷移先の呼び出し設定 menu、footterからの遷移を主とする
 */
@Controller
public class MypageController {
    @Autowired
    HttpSession session;
    @Autowired
    private LoginService loginService;
    @Autowired
    private TbRsvMapper TbRsvMpr;
    @Autowired
    private LoginMapper LoginMpr;

    /**
     * .「ﾏｲﾍﾟｰｼﾞ」画面の呼び出し設定
     *
     * @param mav ModelAndView型
     * @param Myform Myform型
     * @param pageabl ページング
     * @return mypage.html
     */
    @RequestMapping("/web/my/mypage")
    public ModelAndView mypage(ModelAndView mav, MyForm Myform, Pageable pageabl) {

        // //usr認証
        loginService.usrLogin(mav);

        String usrNo = (String) session.getAttribute("usr");

        List<TmUsrEntity> Usrlist = LoginMpr.selectUsrlist(usrNo);
        mav.addObject("usr", Usrlist);
        mav.addObject("usrNo", usrNo);


        // 予約リストの作成

        // 当日の日付を取得
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime ldt = LocalDateTime.now();

        // 現在の予約一覧
        List<MypageEntity> rsv = TbRsvMpr.fetchAllRsvInfo(usrNo, Integer.parseInt(ldt.format(dtf)));
        if (rsv.isEmpty()) {
            mav.addObject("rsv", null);
        } else {
            mav.addObject("rsv", rsv);
        }

        var page = 0;
        var limit = 1;
        var start = 0;
        var activetab = 0;
        double max_page = 0;
        activetab = Myform.getActivetab() == null ? 0 : Myform.getActivetab();
        page = Myform.getPage() == null ? 0 : Myform.getPage();
        limit = Myform.getLimit() == null ? 10 : Myform.getLimit();

        // 選択されたページ、表示件数から取得する開始件数を設定
        start = page * limit;
        // 過去の予約履歴
        List<MypageEntity> rsv_his =
                TbRsvMpr.fetchAllRsvInfo_his(usrNo, Integer.parseInt(ldt.format(dtf)),limit,start);
        if (rsv_his.isEmpty()) {
            mav.addObject("rsv_his", null);
        } else {
            mav.addObject("rsv_his", rsv_his);
        }

        // ページング
        // 過去の予約件数チェック
        Integer max_count = TbRsvMpr.fetchAllRsvInfo_his(usrNo, Integer.parseInt(ldt.format(dtf)),null,null).size();
        if (start > max_count) {
            start = 0;
        }

        // 検索結果の件数から最大ページ数を取得
        max_page = Math.ceil((double) max_count / (double) limit);
        if (page > max_page) {
            page = 0;
        }
        // ページ情報の設定
        mav.addObject("max_count", max_count);
        mav.addObject("max_page", max_page);
        mav.addObject("page", page);
        mav.addObject("limit", limit);
        mav.addObject("start", start + 1);
        mav.addObject("t", activetab);

        mav.addObject("Myform", Myform);
        mav.setViewName("/views/my/mypage");
        return mav;
    }


}
