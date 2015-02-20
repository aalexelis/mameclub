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
      marginTop: - this.$image.height() / 2,
      marginLeft: - this.$image.width() / 2,
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
      marginTop: - this.$image.height() / 2,
      marginLeft: - this.$image.width() / 2,
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
  this.$image = this.$face.find('.nominee-face');
  this.srcFace = this.$image.attr('src');
  this.$scope = null;

  this.init();
};
Nominee.prototype = {
  init: function () {
    this.bindEvents();
  },
  bindEvents: function () {
    var _this = this;

  },
  changeFace2Mame: function () {
    if (/normal/.test(this.srcFace)) {
      this.srcFace = this.srcFace.replace(/normal/, 'pain');
    } else if (/lough/.test(this.srcFace)) {
      this.srcFace = this.srcFace.replace(/lough/, 'pain');
    } else {
      return;
    }

	console.log(this.srcFace);

    this.$image.attr('src', this.srcFace);

    setTimeout($.proxy(function () {
      this.changeFace2Normal();
    }, this), 1000);
  },
  changeFace2Kiss: function () {
    if (/normal/.test(this.srcFace)) {
      this.srcFace = this.srcFace.replace(/normal/, 'lough');
    } else if (/pain/.test(this.srcFace)) {
      this.srcFace = this.srcFace.replace(/pain/, 'lough');
    }

    this.$image.attr('src', this.srcFace);

    setTimeout($.proxy(function () {
      this.changeFace2Normal();
    }, this), 1000);
  },
  changeFace2Normal: function () {
    if (/pain/.test(this.srcFace)) {
      this.srcFace = this.srcFace.replace(/pain/, 'normal');
    } else if (/lough/.test(this.srcFace)) {
      this.srcFace = this.srcFace.replace(/lough/, 'normal');
    }

    this.$image.attr('src', this.srcFace);
  }
};

angular.module('myNominee', [])
    .controller('myNominee.Ctrl', ['$scope', '$document', function($scope, $document) {

      _this = new Nominee();

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

          if ((data.mb + data.fb) % 50 === 0) {
            _this.changeFace2Mame();
          }
        } else {
          new Kiss(data.x, data.y);
          if ((data.mk + data.fk) % 50 === 0) {
            _this.changeFace2Kiss();
          }
        }
        $scope.$digest();
      });
    }]);
