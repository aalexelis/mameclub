var Splash = function () {
  this.$splash = $('#jsi-splash');

  this.init();
};
Splash.prototype = {
  init: function () {
    this.remove();
  },
  remove: function () {
    var _this = this;

    setTimeout(function () {
      _this.$splash.fadeOut(500);
    }, 2000);
  }
};

var Name = function () {
  this.$name = $('#jsi-name');
  this.$text = $('#jsi-name-text');
  this.$submit = this.$name.find('input[type=submit]');

  this.init();
};
Name.prototype = {
  init: function () {
    this.bindEvents();
  },
  bindEvents: function () {
    var _this = this;

    this.$submit.on('click', function () {
      new FaceToucher(_this.$text.val());
      _this.remove();
    });
  },
  remove: function () {
    var _this = this;

    _this.$name.fadeOut(500);
  }
};

var FaceToucher = function (name) {
  this.$face = $('#jsi-main-face');
  this.$mode = $('.jsc-mode').find('input');
  this.countMame = 0;
  this.countKiss = 0;
  this.$countMame = $('#jsi-count-mame').find('span');
  this.$countKiss = $('#jsi-count-kiss').find('span');
  this.name = name;
  this.$name = $('#jsi-name-view').find('span');

  this.init();
};
FaceToucher.prototype = {
  init: function () {
    this.$name.text(this.name);
    this.bindEvents();
  },
  bindEvents: function () {
    var _this = this;

    this.$face.on('touchend', function (e) {
      _this.touchFace(e);
    });
  },
  touchFace: function (e) {
    var
      touchX = e.originalEvent.changedTouches[0].pageX,
      touchY = e.originalEvent.changedTouches[0].pageY;

    touchX = (touchX - this.$face.offset().left) / this.$face.width();
    touchY = (touchY - this.$face.offset().top) / this.$face.height();

    this.sendToTouchStatus(touchX, touchY);

    e.preventDefault();
  },
  sendToTouchStatus: function (touchX, touchY) {
    var mode = this.$mode.filter(':checked').val();

    if (mode === 'mame') {
      new Mame(touchX, touchY);
      this.sendToSrv(this.name, touchX, touchY, "b");
      this.countUp();
    } else {
      new Kiss(touchX, touchY);
      this.sendToSrv(this.name, touchX, touchY, "k");
      this.countUp(true);
    }
  },
  countUp: function (flagKiss) {
    var
    count,
    $elementCount;

    if (flagKiss) {
      this.countKiss++;
      count = this.countKiss;
      $elementCount = this.$countKiss;
    } else {
      this.countMame++;
      count = this.countMame;
      $elementCount = this.$countMame;
    }

    $elementCount.text(count);
  },
  sendToSrv: function (name, x, y, bk) {
    //TODO Temporarily vote sent to nominee="2" from male/famale="m" FIX THIS TOGETHER WITH FRONT-END
    var vote = {nominee: "2",  mf: "m", x: x, y: y, bk: bk}
    $.ajax({
      type: "POST",
      url: 'vote',
      data: JSON.stringify(vote),
      contentType: "application/json;charset=UTF-8",
      dataType: 'json'
    });
  }
};

var Mame = function (x, y) {
  this.$body = $(document.body);
  this.$face = $('#jsi-nominee-face');
  this.$faceFrame = this.$face.find('.jsc-nominee-face-frame');
  this.$image = $('<img src="/assets/images/mame.png" />').addClass('mame');
  this.x = x;
  this.y = y;
  this.defaultX = x;
  this.defaultY = y;
  this.size = 30 + Math.random() * 10;
  this.rotate = Math.random() * 360;

  this.init();
};
Mame.prototype = {
  init: function () {
    this.positioningMame();
  },
  positioningMame: function () {
    this.$faceFrame.append(this.$image);
    this.$image.width(this.size);

    this.x = (this.x * 100) + '%';
    this.y = (this.y * 100) + '%';

    this.$image.css({
      top: this.y,
      left: this.x,
      marginTop: this.$image.height() / 2,
      marginLeft: this.$image.width() / 2,
      transform: 'rotate(' + this.rotate + 'deg)'
    });

    this.$image.stop().animate({
      opacity: 0
    }, 300, function () {
      $(this).remove();
    });
  }
};

var Kiss = function (x, y) {
  this.$body = $(document.body);
  this.$face = $('#jsi-nominee-face');
  this.$faceFrame = this.$face.find('.jsc-nominee-face-frame');
  this.$image = $('<img src="/assets/images/kiss.png" />').addClass('kiss');
  this.x = x;
  this.y = y;
  this.defaultX = x;
  this.defaultY = y;
  this.size = 30 + Math.random() * 10;
  this.rotate = Math.random() * 360;

  this.init();
};
Kiss.prototype = {
  init: function () {
    this.positioningKiss();
  },
  positioningKiss: function () {
    this.$faceFrame.append(this.$image);
    this.$image.width(this.size);

    this.x = (this.x * 100) + '%';
    this.y = (this.y * 100) + '%';

    this.$image.css({
      top: this.y,
      left: this.x,
      marginTop: this.$image.height() / 2,
      marginLeft: this.$image.width() / 2,
      transform: 'rotate(' + this.rotate + 'deg)'
    });

    this.$image.stop().animate({
      opacity: 0
    }, 300, function () {
      $(this).remove();
    });
  }
};

var Nominee = function () {
  this.$face = $('#jsi-nominee-face');
  this.$scope = null;

  this.init();
};
Nominee.prototype = {
  init: function () {
    this.bindEvents();
  },
  bindEvents: function () {
    var _this = this;

    angular.module('myNominee', [])
    .controller('myNominee.Ctrl', ['$scope', '$document', function($scope, $document) {
      $scope.vote = {
        nominee: null,
        mf: null,
        x: null,
        y: null,
        bk: null
      }
      $scope.stats = {
        nominee: null,
        mb: null,
        mk: null,
        fb: null,
        fk: null
      }
      _this.$scope = $scope;

      $document
      .on('new-ng-vote', function(event, data) {
        $scope.vote.mf = data.mf;
        $scope.vote.x = data.x;
        $scope.vote.y = data.y;
        $scope.vote.bk = data.bk;
        $scope.stats.mb = data.mb;
        $scope.stats.mk = data.mk;
        $scope.stats.fb = data.fb;
        $scope.stats.fk = data.fk;

        if ($scope.vote.bk === 'b') {
          new Mame(data.x, data.y);
        } else {
          new Kiss(data.x, data.y);
        }

        $scope.$digest();
      });
    }]);
  },
  changeFace2Mame: function () {
  },
  changeFace2Kiss: function () {
  }
};

$(function () {
  // new Splash();
  new Name();
  new Nominee();
});
