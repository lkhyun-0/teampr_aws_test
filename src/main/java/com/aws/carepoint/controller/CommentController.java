package com.aws.carepoint.controller;

import com.aws.carepoint.dto.CommentDto;
import com.aws.carepoint.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/addComment")
    public ResponseEntity<CommentDto> addComment(
            @RequestBody CommentDto commentDto
    ) {
        CommentDto comment = commentService.addComment(commentDto);

        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/deleteComment")
    public ResponseEntity<?> deleteComment(
            @RequestParam("commentPk") int commentPk, // ✅ @RequestParam 사용
            HttpSession session) {

        Integer userPk = (Integer) session.getAttribute("userPk");

        // 비로그인 사용자 차단
        if (userPk == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 댓글 존재 여부 확인
        CommentDto comment = commentService.getCommentById(commentPk);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 댓글입니다.");
        }

        // 본인의 댓글인지 확인
        if (!comment.getUserPk().equals(userPk)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인의 댓글만 삭제할 수 있습니다.");
        }

        // 삭제 실행
        int result = commentService.deleteComment(commentPk);
        if (result > 0) {
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 실패");
        }
    }
}
